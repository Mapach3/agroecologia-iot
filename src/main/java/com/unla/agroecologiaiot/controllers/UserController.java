package com.unla.agroecologiaiot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.agroecologiaiot.models.ApplicationUserModel;
import com.unla.agroecologiaiot.services.IApplicationUserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;

@RestController
@RequestMapping("api/v1/users")
public class UserController {

    @Autowired
    @Qualifier("applicationUserService")
    private IApplicationUserService applicationUserService;

    @PostMapping("")
    @SecurityRequirements // disables Security Schemes from Configuration. @SecurityRequirement to
                          // override global config?
    public ResponseEntity<Long> signUp(@RequestBody ApplicationUserModel model) {
        long response = applicationUserService.saveOrUpdate(model);
        return new ResponseEntity<Long>(response, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ResponseEntity get(@PathVariable long id) {

        ApplicationUserModel userModel = applicationUserService.getUser(id);

        if (userModel != null) {
            return new ResponseEntity(userModel, HttpStatus.OK);
        }

        return new ResponseEntity("User Not Found", HttpStatus.NOT_FOUND);
    }

}
