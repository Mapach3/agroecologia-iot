package com.unla.agroecologiaiot.services.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Crop;
import com.unla.agroecologiaiot.helpers.FilterHelper.Filter;
import com.unla.agroecologiaiot.helpers.MessageHelper.Message;
import com.unla.agroecologiaiot.helpers.PageHelper.Paged;
import com.unla.agroecologiaiot.models.CropModel;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.CropRepository;
import com.unla.agroecologiaiot.services.ICropService;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;
import com.unla.agroecologiaiot.shared.paginated.PaginatedList;
import com.unla.agroecologiaiot.shared.paginated.especification.FilterRequest;
import com.unla.agroecologiaiot.shared.paginated.SearchEspecification;
import com.unla.agroecologiaiot.shared.paginated.especification.FieldType;
import com.unla.agroecologiaiot.shared.paginated.PagerParameters;

@Service("cropService")
public class CropService implements ICropService {
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    @Qualifier("cropRepository")
    private CropRepository cropRepository;

    @Autowired
    @Qualifier("applicationUserRepository")
    private ApplicationUserRepository applicationUserRepository;

    @Override
    public ResponseEntity<String> saveOrUpdate(CropModel model, long idOwner) {
        try {
            Optional<Crop> dbCrop = cropRepository.findByName(model.getName());

            if (dbCrop.isPresent()) {
                return Message.ErrorValidation();
            }

            ApplicationUser user = applicationUserRepository.getById(idOwner);

            if (user == null) {
                return Message.ErrorValidation();
            }

            model.setCropId(0);
            Crop crop = modelMapper.map(model, Crop.class);
            crop.setOwner(user);

            long response = cropRepository.save(crop).getCropId();

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> put(CropModel model, long id) {
        try {
            Crop crop = cropRepository.getById(id);

            if (crop == null) {
                return Message.ErrorSearchEntity();
            }

            crop.setName(model.getName());

            long response = cropRepository.save(crop).getCropId();

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> delete(long id) {
        try {
            Crop crop = cropRepository.getById(id);

            if (crop == null) {
                return Message.ErrorSearchEntity();
            }

            crop.setDeleted(true);
            cropRepository.save(crop);

            return Message.Ok(true);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> getById(long id) {
        try {
            Optional<Crop> crop = cropRepository.findByCropIdAndIsDeleted(id, false);

            if (crop.isPresent()) {
                CropModel cropModel = modelMapper.map(crop, CropModel.class);

                return Message.Ok(cropModel);
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

            PaginatedList<CropModel> paginatedList = new PaginatedList<>();
            List<FilterRequest> filters = new ArrayList<FilterRequest>();
            filters.add(Filter.AddFilterPropertyEqual("isDeleted", false, FieldType.BOOLEAN));

            if (!isAdmin) { 
                filters.add(Filter.AddFilterPropertyEqual("owner", idUser, FieldType.LONG));
            }

            pageParameters.setFilters(filters);
            SearchEspecification<Crop> especification = new SearchEspecification<>(pageParameters);
            Page<Crop> dbCrop = cropRepository.findAll(especification, page);

            List<CropModel> cropModels = new ArrayList<CropModel>();

            for (Crop crop : dbCrop.toList()) {
                CropModel cropModel = modelMapper.map(crop, CropModel.class);
                cropModels.add(cropModel);
            }

            paginatedList.setList(cropModels);
            paginatedList.setCount(dbCrop.getTotalElements());                                                       
            paginatedList.setIndex(dbCrop.getNumber());

            return Message.Ok(paginatedList);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

}
