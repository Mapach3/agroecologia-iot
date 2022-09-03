package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.CropModel;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;

public interface ICropService {
    
    public ResponseEntity<String> saveOrUpdate(CropModel model, long idOwner);
    public ResponseEntity<String> put(CropModel model, long id);
    public ResponseEntity<String> delete(long id);
    public ResponseEntity<String> getById(long id);
    public ResponseEntity<String> getList(PagerParametersModel pageParameters, boolean isAdmin, long idUser);
}
