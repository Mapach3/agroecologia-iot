package com.unla.agroecologiaiot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.unla.agroecologiaiot.models.RoleModel;
import com.unla.agroecologiaiot.services.IRoleService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("api/v1/role")
public class RoleController {
    
    @Autowired
    @Qualifier("roleService")
    private IRoleService roleService;
  
    @PutMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> put(@RequestBody RoleModel roleModel) {
        return roleService.put(roleModel);
    }

    @GetMapping("all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<String> getAll() {
        return roleService.getAll();
    }
}
