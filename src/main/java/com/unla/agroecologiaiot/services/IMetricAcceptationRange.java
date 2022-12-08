package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.MetricAcceptationRangeModel;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;

public interface IMetricAcceptationRange {
    
    public ResponseEntity<String> saveOrUpdate(MetricAcceptationRangeModel model, long idOwner);
    public ResponseEntity<String> put(MetricAcceptationRangeModel model, long id);
    public ResponseEntity<String> delete(long id);
    public ResponseEntity<String> getById(long id);
    public ResponseEntity<String> garden(PagerParametersModel pageParametersModel, boolean isAdmin, long idUser);
}
