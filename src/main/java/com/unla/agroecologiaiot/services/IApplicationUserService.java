package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.ApplicationUserModel;

public interface IApplicationUserService {

    public ResponseEntity<String> saveOrUpdate(ApplicationUserModel model);
    public ResponseEntity<String> put(ApplicationUserModel model, long id);
    public ResponseEntity<String> delete(long id);
    public ResponseEntity<String> getById(long id);
    public ResponseEntity<String> getByUsername(String username);
    public ResponseEntity<String> getListByRoleId(long id);
    public ResponseEntity<String> getList();

    public ResponseEntity<String> logout(String token);

}
