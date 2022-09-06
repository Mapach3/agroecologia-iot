package com.unla.agroecologiaiot.services.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Garden;
import com.unla.agroecologiaiot.entities.Sector;
import com.unla.agroecologiaiot.helpers.FilterHelper.Filter;
import com.unla.agroecologiaiot.helpers.MessageHelper.Message;
import com.unla.agroecologiaiot.models.GardenModel;
import com.unla.agroecologiaiot.models.SectorModel;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.CropRepository;
import com.unla.agroecologiaiot.repositories.GardenRepository;
import com.unla.agroecologiaiot.repositories.SectorRepository;
import com.unla.agroecologiaiot.services.IGardenService;
import com.unla.agroecologiaiot.shared.paginated.PagerParameters;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;
import com.unla.agroecologiaiot.shared.paginated.PaginatedList;
import com.unla.agroecologiaiot.shared.paginated.SearchEspecification;
import com.unla.agroecologiaiot.shared.paginated.especification.FieldType;
import com.unla.agroecologiaiot.shared.paginated.especification.FilterRequest;
import com.unla.agroecologiaiot.helpers.ModelMapperHelper.MappingHelper;
import com.unla.agroecologiaiot.helpers.PageHelper.Paged;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service("gardenService")
public class GardenService implements IGardenService {
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    @Qualifier("gardenRepository")
    private GardenRepository gardenRepository;

    @Autowired
    @Qualifier("applicationUserRepository")
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    @Qualifier("sectorRepository")
    private SectorRepository sectorRepository;

    @Autowired
    @Qualifier("cropRepository")
    private CropRepository cropRepository;

    @Override
    public ResponseEntity<String> saveOrUpdate(GardenModel model, long idOwner) {
        try {
            Optional<Garden> dbGarden = gardenRepository.findByName(model.getName());

            if (dbGarden.isPresent()) {
                return Message.ErrorValidation();
            }

            ApplicationUser user = applicationUserRepository.getById(idOwner);

            if (user == null) {
                return Message.ErrorValidation();
            }

            model.setGardenId(0);
            Garden garden = modelMapper.map(model, Garden.class);
            garden.setOwner(user);

            long response = gardenRepository.save(garden).getGardenId();

            Garden gardenInserted = gardenRepository.getById(response);

            List<Sector> sectors = MappingHelper.mapList(model.getSectors(), Sector.class);
            for (Sector sector : sectors) {
                sector.setGarden(gardenInserted);
                var crops = cropRepository.findAllById(sector.getCropIds());
                sector.setSectorCrops(Set.copyOf(crops));
            }

            sectorRepository.saveAll(sectors);

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> put(GardenModel model, long id) {
        try {
            Garden garden = gardenRepository.getById(id);
          
            if (garden == null) {
                return Message.ErrorSearchEntity();
            }

            garden.setSectors(null);
            garden.setDescription(model.getDescription());
            garden.setName(model.getName());
            garden.setLocation(model.getLocation());
            long response = gardenRepository.save(garden).getGardenId();

            List<Sector> sectors = MappingHelper.mapList(model.getSectors(), Sector.class);
            for (Sector sector : sectors) {
                sector.setGarden(garden);
                var crops = cropRepository.findAllById(sector.getCropIds());
                sector.setSectorCrops(Set.copyOf(crops));
            }

            sectorRepository.saveAll(sectors);

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> delete(long id) {
        try {
            Garden garden = gardenRepository.getById(id);
            List<Sector> sectors = sectorRepository.findByGarden(garden);

            if (garden == null || sectors == null) {
                return Message.ErrorSearchEntity();
            }

            garden.setDeleted(true);
            gardenRepository.save(garden);

            for (Sector sector : sectors) {
                sector.setDeleted(true);
                sector.setSectorCrops(null);
            }

            sectorRepository.saveAll(sectors);

            return Message.Ok(true);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> getById(long id) {
        try {
            Optional<Garden> garden = gardenRepository.findByGardenIdAndIsDeleted(id, false);

            if (garden.isPresent()) {
                GardenModel gardenModel = modelMapper.map(garden, GardenModel.class);

                List<Sector> sectorsList = new ArrayList<>(garden.get().getSectors());
                gardenModel.setSectors(MappingHelper.mapList(sectorsList, SectorModel.class));

                for (SectorModel sectorModel : gardenModel.getSectors()) {           
                    sectorModel.setCropIds(sectorModel.getSectorCrops().stream()
                    .flatMap(x -> Stream.of(x.getCropId()))
                    .collect(Collectors.toList()));
                }

                return Message.Ok(gardenModel);
            }

            return Message.ErrorSearchEntity();

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> getList(PagerParametersModel pageParametersModel, boolean isAdmin, long idUser) {
        try {
            PagerParameters pageParameters = modelMapper.map(pageParametersModel, PagerParameters.class);

            if (pageParameters.getPageSize() == 0) {
                pageParameters.setPageSize(10);
            }

            Pageable page = Paged.CreatePage(pageParameters);

            if (page == null) {
                return Message.ErrorValidation();
            }

            PaginatedList<GardenModel> paginatedList = new PaginatedList<>();
            List<FilterRequest> filters = new ArrayList<FilterRequest>();
            filters.add(Filter.AddFilterPropertyEqual("isDeleted", false, FieldType.BOOLEAN));

            if (!isAdmin) {
                filters.add(Filter.AddFilterPropertyEqual("owner", idUser, FieldType.LONG));
            }

            pageParameters.setFilters(filters);
            SearchEspecification<Garden> especification = new SearchEspecification<>(pageParameters);
            Page<Garden> dbGarden = gardenRepository.findAll(especification, page);

            List<GardenModel> gardenModels = new ArrayList<GardenModel>();

            for (Garden garden : dbGarden.toList()) {
                GardenModel gardenModel = modelMapper.map(garden, GardenModel.class);

                List<Sector> sectorsList = new ArrayList<>(garden.getSectors());
                gardenModel.setSectors(MappingHelper.mapList(sectorsList, SectorModel.class));
                
                for (SectorModel sectorModel : gardenModel.getSectors()) {           
                    sectorModel.setCropIds(sectorModel.getSectorCrops().stream()
                    .flatMap(x -> Stream.of(x.getCropId()))
                    .collect(Collectors.toList()));
                }
                
                gardenModels.add(gardenModel);
            }

            

            paginatedList.setList(gardenModels);
            paginatedList.setCount(dbGarden.getTotalElements());
            paginatedList.setIndex(dbGarden.getNumber());

            return Message.Ok(paginatedList);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

}
