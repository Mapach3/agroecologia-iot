package com.unla.agroecologiaiot.services.implementation;

import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Role;
import com.unla.agroecologiaiot.models.ApplicationUserModel;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.RoleRepository;
import com.unla.agroecologiaiot.services.IApplicationUserService;

import static java.util.Collections.emptyList;

@Service("applicationUserDetailsService")
public class ApplicationUserDetailsService implements UserDetailsService, IApplicationUserService {

    private ModelMapper modelMapper = new ModelMapper();
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    @Qualifier("applicationUserRepository")
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    @Qualifier("roleRepository")
    private RoleRepository roleRepository;

    @Override
    public long saveOrUpdate(ApplicationUserModel model) {
        ApplicationUser user = modelMapper.map(model, ApplicationUser.class);

        Role role = roleRepository.findById(model.getRoleId()).get();
        user.setRole(role);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        return applicationUserRepository.save(user).getUserId();
    }

    @Override
    public ApplicationUserModel getUser(long id) {
        Optional<ApplicationUser> dbUser = applicationUserRepository.findByIdAndFetchRoleEagerly(id);

        if (dbUser.isPresent()) {
            return modelMapper.map(dbUser.get(), ApplicationUserModel.class);
        }
        return null;
    }

    public ProfileDTO getProfile(String username) {

        Optional<ApplicationUser> dbUser = applicationUserRepository.findByUsername(username);

        if (dbUser.isPresent()) {
            return modelMapper.map(dbUser.get(), ProfileDTO.class);
        }
        return null;

    }

    // Auth methods (from UserDetailsService)

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<ApplicationUser> applicationUser = applicationUserRepository.findByUsername(username);

        if (!applicationUser.isPresent()) {
            throw new UsernameNotFoundException("Username not found");
        }

        return new User(applicationUser.get().getUsername(), applicationUser.get().getPassword(), emptyList());
    }

}
