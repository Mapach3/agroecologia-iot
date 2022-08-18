package com.unla.agroecologiaiot.services;

import com.unla.agroecologiaiot.models.ApplicationUserModel;

public interface IApplicationUserService {

    public long saveOrUpdate(ApplicationUserModel model);

    public ApplicationUserModel getUser(long id);

    public ApplicationUserModel getUser(String username);

    public boolean logout(String token);

}
