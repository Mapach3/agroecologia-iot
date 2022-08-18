package com.unla.agroecologiaiot.services.implementation;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.unla.agroecologiaiot.Helpers.HttpHelper.Http;
import com.unla.agroecologiaiot.Helpers.JsonParse.JsonParser;
import com.unla.agroecologiaiot.constants.SecurityConstants;
import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Session;
import com.unla.agroecologiaiot.models.auth.LoginResponse;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.SessionRepository;
import com.unla.agroecologiaiot.services.ITokenService;

@Service("tokenService")
public class TokenService implements ITokenService {

    @Autowired
    @Qualifier("applicationUserRepository")
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    @Qualifier("sessionRepository")
    private SessionRepository sessionRepository;

    public Claims createClaims(ApplicationUser user) {

        Map<String, Object> customClaims = new HashMap<String, Object>();
        customClaims.put(SecurityConstants.CustomSecurityClaims.ROLE, user.getRole().getCode());
        customClaims.put(SecurityConstants.CustomSecurityClaims.EMAIL, user.getEmail());
        customClaims.put(SecurityConstants.CustomSecurityClaims.USERNAME, user.getUsername());

        return Jwts.claims(customClaims);
    }

    public String createToken(ApplicationUser user, Date exp) {

        Key key = Keys.hmacShaKeyFor(SecurityConstants.SECRET.getBytes());

        return Jwts.builder()
                .setClaims(createClaims(user))
                .setSubject(Long.toString(user.getUserId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public ResponseEntity<String> refreshToken(Date exp, LocalDateTime dateExpires, String oldToken) {

        try {
            Optional<Session> session = sessionRepository.findByToken(oldToken);

            if (!session.isPresent()) {
                return new ResponseEntity<String>(JsonParser.ToJson("Error de validacion."), Http.GetContentType_Json(),
                        HttpStatus.BAD_REQUEST);
            }

            ApplicationUser user = session.get().getUser();

            String token = createToken(user, exp);

            if (session.isPresent()) {
                session.get().setToken(token);
                session.get().setIssuedAt(LocalDateTime.now());
                session.get().setExpiresAt(dateExpires);

                sessionRepository.save(session.get());
            }

            ProfileDTO profile = ProfileDTO.builder().username(user.getUsername()).name(user.getName())
                    .surname(user.getSurname()).roleCode(user.getRole().getCode()).build();

            LoginResponse response = new LoginResponse(token, profile, dateExpires.toString());

            return new ResponseEntity<String>(JsonParser.ToJson(response), Http.GetContentType_Json(), HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<String>(JsonParser.ToJson("Ups! Algo salio mal."), Http.GetContentType_Json(),
                    HttpStatus.BAD_REQUEST);
        }
    }

}
