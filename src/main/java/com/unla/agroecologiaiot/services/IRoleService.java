package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.RoleModel;

public interface IRoleService {
    public ResponseEntity<String> put(RoleModel roleModel, long id);
    public ResponseEntity<String> getAll();
}
