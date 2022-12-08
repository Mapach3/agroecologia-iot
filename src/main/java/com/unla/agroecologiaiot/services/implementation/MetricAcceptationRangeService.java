package com.unla.agroecologiaiot.services.implementation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.MetricAcceptationRange;
import com.unla.agroecologiaiot.entities.MetricType;
import com.unla.agroecologiaiot.entities.Sector;
import com.unla.agroecologiaiot.helpers.FilterHelper.Filter;
import com.unla.agroecologiaiot.helpers.MessageHelper.Message;
import com.unla.agroecologiaiot.helpers.PageHelper.Paged;
import com.unla.agroecologiaiot.models.MetricAcceptationRangeModel;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.MetricAcceptationRangeRepository;
import com.unla.agroecologiaiot.repositories.MetricTypeRepository;
import com.unla.agroecologiaiot.repositories.SectorRepository;
import com.unla.agroecologiaiot.services.IMetricAcceptationRange;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;
import com.unla.agroecologiaiot.shared.paginated.PaginatedList;
import com.unla.agroecologiaiot.shared.paginated.especification.FilterRequest;
import com.unla.agroecologiaiot.shared.paginated.SearchEspecification;
import com.unla.agroecologiaiot.shared.paginated.especification.FieldType;
import com.unla.agroecologiaiot.shared.paginated.PagerParameters;

@Service("metricAcceptationRangeService")
public class MetricAcceptationRangeService implements IMetricAcceptationRange {
    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    @Qualifier("metricAcceptationRangeRepository")
    private MetricAcceptationRangeRepository metricAcceptationRangeRepository;

    @Autowired
    @Qualifier("sectorRepository")
    private SectorRepository sectorRepository;

    @Autowired
    @Qualifier("applicationUserRepository")
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    @Qualifier("metricTypeRepository")
    private MetricTypeRepository metricTypeRepository;

    @Override
    public ResponseEntity<String> saveOrUpdate(MetricAcceptationRangeModel model, long idOwner) {
        try {
            if(!(model.getStartValue() > 0 && model.getEndValue() < 100)){
                return Message.ErrorValidation();
            }

            Optional<MetricAcceptationRange> dbMetricAcceptationRange = metricAcceptationRangeRepository.findByName(model.getName());

            if (dbMetricAcceptationRange.isPresent()) {
                return Message.ErrorValidation();
            }

            ApplicationUser user = applicationUserRepository.getById(idOwner);

            if (user == null) {
                return Message.ErrorValidation();
            }

            MetricType metricType = metricTypeRepository.findByCode(model.getMetricTypeCode());

            if (metricType == null) {
                return Message.ErrorValidation();
            }

            model.setMetricAcceptationRangeId(0);
            MetricAcceptationRange metricAcceptationRange = modelMapper.map(model, MetricAcceptationRange.class);
            metricAcceptationRange.setOwner(user);
            metricAcceptationRange.setMetricType(metricType);

            long response = metricAcceptationRangeRepository.save(metricAcceptationRange).getMetricAcceptationRangeId();

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> put(MetricAcceptationRangeModel model, long id) {
        try {
            MetricAcceptationRange metricAcceptationRange = metricAcceptationRangeRepository.getById(id);

            if (metricAcceptationRange == null) {
                return Message.ErrorSearchEntity();
            }

            metricAcceptationRange.setName(model.getName());
            metricAcceptationRange.setStartValue(model.getStartValue());
            metricAcceptationRange.setEndValue(model.getEndValue());

            long response = metricAcceptationRangeRepository.save(metricAcceptationRange).getMetricAcceptationRangeId();

            return Message.Ok(response);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> delete(long id) {
        try {
            MetricAcceptationRange metricAcceptationRange = metricAcceptationRangeRepository.findByIdAndFetchSectorsEagerly(id);

            if (metricAcceptationRange == null) {
                return Message.ErrorSearchEntity();
            }

            metricAcceptationRange.setDeleted(true);

            for (Sector sector : metricAcceptationRange.getSectors()) { //TODO: VALIDAR CON GUIDO Y ENTENDER QUE HACE EXACTAMENTE
                sector.setMetricAcceptationRanges(
                        sector.getMetricAcceptationRanges().stream().filter(sectorMetric -> 
                            sectorMetric.getMetricAcceptationRangeId() != metricAcceptationRange.getMetricAcceptationRangeId())
                                .collect(Collectors.toSet()));
            }

            metricAcceptationRangeRepository.save(metricAcceptationRange);


            return Message.Ok(true);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> getById(long id) {
        try {
            Optional<MetricAcceptationRange> metricAcceptationRange = metricAcceptationRangeRepository.findByMetricAcceptationRangeIdAndIsDeleted(id, false);

            if (metricAcceptationRange.isPresent()) {
                MetricAcceptationRangeModel metricAcceptationRangeModel = modelMapper.map(metricAcceptationRange, MetricAcceptationRangeModel.class);
                return Message.Ok(metricAcceptationRangeModel);
            }

            return Message.ErrorSearchEntity();

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }

    @Override
    public ResponseEntity<String> garden(PagerParametersModel pageParametersModel, boolean isAdmin, long idUser) {
        try {
            PagerParameters pageParameters = modelMapper.map(pageParametersModel, PagerParameters.class);

            if (pageParameters.getPageSize() == 0) {
                pageParameters.setPageSize(10);
            }

            Pageable page = Paged.CreatePage(pageParameters);

            if (page == null) {
                return Message.ErrorValidation();
            }

            PaginatedList<MetricAcceptationRangeModel> paginatedList = new PaginatedList<>();
            List<FilterRequest> filters = new ArrayList<FilterRequest>();
            filters.add(Filter.AddFilterPropertyEqual("isDeleted", false, FieldType.BOOLEAN));

            if (!isAdmin) { //TODO: DUDA SOBRE EL FILTRADO, CONSULTAR A GUIDO, SINO ES ARMAR ESTE FILTRO SIEMPRE Y VA A FILTRAR DEPENDIENDO DEL ROL ENTIENDO
                filters.add(Filter.AddFilterPropertyEqual("owner", idUser, FieldType.LONG));
            }

            pageParameters.setFilters(filters);
            SearchEspecification<MetricAcceptationRange> especification = new SearchEspecification<>(pageParameters);
            Page<MetricAcceptationRange> dbMetricAcceptationRange = metricAcceptationRangeRepository.findAll(especification, page);

            List<MetricAcceptationRangeModel> metricAcceptationRangeModels = new ArrayList<MetricAcceptationRangeModel>();

            for (MetricAcceptationRange metricAcceptationRange : dbMetricAcceptationRange.toList()) {
                MetricAcceptationRangeModel metricAcceptationRangeModel = modelMapper.map(metricAcceptationRange, MetricAcceptationRangeModel.class);
                metricAcceptationRangeModels.add(metricAcceptationRangeModel);
            }

            paginatedList.setList(metricAcceptationRangeModels);
            paginatedList.setCount(dbMetricAcceptationRange.getTotalElements());
            paginatedList.setIndex(dbMetricAcceptationRange.getNumber());

            return Message.Ok(paginatedList);

        } catch (Exception e) {
            return Message.ErrorException(e);
        }
    }
}
