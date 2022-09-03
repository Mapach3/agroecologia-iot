package com.unla.agroecologiaiot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;

import com.unla.agroecologiaiot.helpers.SecurityContextHelper.SecurityContext;
import com.unla.agroecologiaiot.models.CropModel;
import com.unla.agroecologiaiot.services.ICropService;
import com.unla.agroecologiaiot.shared.paginated.PagerParametersModel;

@RestController
@RequestMapping("api/v1/crops")
public class CropController {
    
    @Autowired
    @Qualifier("cropService")
    private ICropService cropService;

    @PostMapping("")
    @PreAuthorize("hasAuthority('ADMIN')" + "|| hasAuthority('GARDEN_MANAGER')")
    public ResponseEntity<String> post(@RequestBody CropModel model) {
        return cropService.saveOrUpdate(model, SecurityContext.getUserIdContext().get());
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')" + "|| hasAuthority('GARDEN_MANAGER')")
    public ResponseEntity<String> put(@RequestBody CropModel model, @PathVariable long id) {
        return cropService.put(model, id);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')" + "|| hasAuthority('GARDEN_MANAGER')")
    public ResponseEntity<String> get(@PathVariable long id) {
        return cropService.getById(id);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAuthority('ADMIN')" + "|| hasAuthority('GARDEN_MANAGER')")
    public ResponseEntity<String> delete(@PathVariable long id) {
        return cropService.delete(id);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')" + "|| hasAuthority('GARDEN_MANAGER')")
    public ResponseEntity<String> getList(PagerParametersModel pageParameters) {
        boolean isAdmin = SecurityContext.getRoleContext().getCode().equals("ADMIN")
                ? true
                : false;
        return cropService.getList(pageParameters, isAdmin, SecurityContext.getUserIdContext().get());
    }

}
