package com.unla.agroecologiaiot.services;

import org.springframework.http.ResponseEntity;

import com.unla.agroecologiaiot.models.ApplicationUserModel;

public interface IApplicationUserService {

    public ResponseEntity<String> saveOrUpdate(ApplicationUserModel model);
    public ResponseEntity<String> put(ApplicationUserModel model, long id);

    public ApplicationUserModel getUser(long id);

    public ApplicationUserModel getUser(String username);

    public ResponseEntity<String> logout(String token);

}
