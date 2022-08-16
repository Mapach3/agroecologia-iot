package com.unla.agroecologiaiot.controllers;

import com.google.gson.Gson;
import com.unla.agroecologiaiot.constants.SecurityConstants;
import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Session;
import com.unla.agroecologiaiot.models.auth.LoginDTO;
import com.unla.agroecologiaiot.models.auth.LoginResponse;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.SessionRepository;
import com.unla.agroecologiaiot.services.ITokenService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

  @Autowired
  @Qualifier("tokenService")
  private ITokenService tokenService;

  @Autowired
  @Qualifier("applicationUserRepository")
  private ApplicationUserRepository applicationUserRepository;

  @Autowired
  @Qualifier("sessionRepository")
  private SessionRepository sessionRepository;

  private Gson gson = new Gson();

  @PostMapping("login")
  @SecurityRequirements
  public void login(@RequestBody @Validated LoginDTO loginDto) {
  }

  @PostMapping("refreshToken")
  public ResponseEntity<String> refreshToken(HttpServletRequest req, Authentication auth) {
    try {
      String oldToken = req.getHeader("Authorization").split(" ")[1].toString();
      Date exp = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
      LocalDateTime dateExpires = exp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

      ApplicationUser user = applicationUserRepository
          .findByUsernameAndFetchRoleEagerly( //Ver como resolver la obtencion de UserDeatail del Token viejo
              ((User) auth.getPrincipal()).getUsername())
          .get();

      String token = tokenService.CreateToken(user, exp);

      Optional<Session> session = sessionRepository.findByToken(oldToken);

      if (session.isPresent()) {
        session.get().setToken(token);
        session.get().setIssuedAt(LocalDateTime.now());
        session.get().setExpiresAt(dateExpires);
        ;

        sessionRepository.save(session.get());
      } else {
        Session sessionCreated = Session.builder()
            .token(token)
            .isActive(true)
            .issuedAt(LocalDateTime.now())
            .expiresAt(dateExpires)
            .user(user)
            .build();

        sessionRepository.save(sessionCreated);
      }

      ProfileDTO profile = ProfileDTO.builder().username(user.getUsername()).name(user.getName())
          .surname(user.getSurname()).roleCode(user.getRole().getCode()).build();

      LoginResponse response = new LoginResponse(token, profile, dateExpires.toString());
      String jsonResponse = this.gson.toJson(response);

      return new ResponseEntity<String>(jsonResponse, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<String>("Ups! Algo salio mal.",HttpStatus.BAD_REQUEST);
    }
  }
}
