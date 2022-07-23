package com.unla.agroecologiaiot.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.agroecologiaiot.models.auth.LoginDTO;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @PostMapping("login")
    @SecurityRequirements
    public void login(@RequestBody @Validated LoginDTO loginDto) {
    }

}
