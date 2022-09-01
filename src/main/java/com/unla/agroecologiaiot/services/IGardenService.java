package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.GardenModel;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;

public interface IGardenService {

    public ResponseEntity<String> saveOrUpdate(GardenModel model, long idOwner);
    public ResponseEntity<String> put(GardenModel model, long id);
    public ResponseEntity<String> delete(long id);
    public ResponseEntity<String> getById(long id);
    public ResponseEntity<String> getList(PagerParametersModel pageParameters);
}
