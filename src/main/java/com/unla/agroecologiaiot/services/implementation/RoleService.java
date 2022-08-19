package com.unla.agroecologiaiot.services.implementation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.unla.agroecologiaiot.entities.Role;
import com.unla.agroecologiaiot.helpers.MessageHelper.Message;
import com.unla.agroecologiaiot.models.RoleModel;
import com.unla.agroecologiaiot.repositories.RoleRepository;
import com.unla.agroecologiaiot.services.IRoleService;

@Service("roleService")
public class RoleService  implements IRoleService {
    
    @Autowired
    @Qualifier("roleRepository")
    private RoleRepository roleRepository;

    public ResponseEntity<String> put(RoleModel roleModel) {
        try {
            Role role = roleRepository.getById(roleModel.getRoleId());

            if(role != null){
                role.setName(roleModel.getName());
                roleRepository.save(role);

                return Message.Ok(role.getRoleId());
            }
            
            return Message.ErrorSearchEntity();

        } catch (Exception e) {
            return Message.ErrorException();
        }
    }

    public ResponseEntity<String> getAll() {
        try {
            List<Role> roles = roleRepository.findAll();

            if(roles.size() > 0){
                return Message.Ok(roles);
            }
            
            return Message.ErrorSearchEntity();

        } catch (Exception e) {
            return Message.ErrorException();
        }
    }
}
