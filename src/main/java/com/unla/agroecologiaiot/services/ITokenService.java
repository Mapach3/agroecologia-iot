package com.unla.agroecologiaiot.services;

import java.util.Date;

import com.unla.agroecologiaiot.entities.ApplicationUser;

import io.jsonwebtoken.Claims;

public interface ITokenService {
    
    public String CreateToken(ApplicationUser user, Date exp);
    public Claims CreateClaims(ApplicationUser user);
}
