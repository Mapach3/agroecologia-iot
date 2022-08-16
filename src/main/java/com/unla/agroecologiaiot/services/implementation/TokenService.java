package com.unla.agroecologiaiot.services.implementation;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.unla.agroecologiaiot.constants.SecurityConstants;
import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.services.ITokenService;

@Service("tokenService")
public class TokenService implements ITokenService {

    public Claims CreateClaims(ApplicationUser user) {

        Map<String, Object> customClaims = new HashMap<String, Object>();
        customClaims.put(SecurityConstants.CustomSecurityClaims.ROLE, user.getRole().getCode());
        customClaims.put(SecurityConstants.CustomSecurityClaims.EMAIL, user.getEmail());
        customClaims.put(SecurityConstants.CustomSecurityClaims.USERNAME, user.getUsername());

        return Jwts.claims(customClaims);
    }

    public String CreateToken(ApplicationUser user, Date exp) {

        Key key = Keys.hmacShaKeyFor(SecurityConstants.SECRET.getBytes());

        return Jwts.builder()
                .setClaims(CreateClaims(user))
                .setSubject(Long.toString(user.getUserId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

}
