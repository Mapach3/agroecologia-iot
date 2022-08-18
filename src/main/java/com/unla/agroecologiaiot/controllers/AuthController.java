package com.unla.agroecologiaiot.controllers;

import com.unla.agroecologiaiot.constants.SecurityConstants;
import com.unla.agroecologiaiot.models.auth.LoginDTO;
import com.unla.agroecologiaiot.services.ITokenService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

  @Autowired
  @Qualifier("tokenService")
  private ITokenService tokenService;

  @PostMapping("login")
  @SecurityRequirements
  public void login(@RequestBody @Validated LoginDTO loginDto) {
  }

  @PostMapping("refreshtoken")
  public ResponseEntity<String> refreshToken(HttpServletRequest req) {
      String oldToken = req.getHeader("Authorization").split(" ")[1].toString();
      Date exp = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
      LocalDateTime dateExpires = exp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

      return tokenService.refreshToken(exp, dateExpires, oldToken);
  }
}
