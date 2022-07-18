package com.unla.agroecologiaiot.services;

import com.unla.agroecologiaiot.models.ApplicationUserModel;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;

public interface IApplicationUserService {

    public long saveOrUpdate(ApplicationUserModel model);

    public ApplicationUserModel getUser(long id);

    public ProfileDTO getProfile(String username);

}
