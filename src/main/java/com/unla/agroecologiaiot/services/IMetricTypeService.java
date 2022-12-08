package com.unla.agroecologiaiot.services;
import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.MetricTypeModel;

public interface IMetricTypeService {
    
    public ResponseEntity<String> getById(long id);

    public ResponseEntity<String> getAll();

    public ResponseEntity<String> put(MetricTypeModel metricTypeModel, long id);
}
