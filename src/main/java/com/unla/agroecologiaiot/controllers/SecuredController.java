package com.unla.agroecologiaiot.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/secure")
public class SecuredController {

    @GetMapping("")
    public ResponseEntity<String> getSecuredEndpoint() {

        return new ResponseEntity<String>(
                "If you are seeing this, you have reached a secured endpoint!",
                HttpStatus.OK);
    }

}
