package com.unla.agroecologiaiot.services.implementation;

import static java.util.Collections.emptyList;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Role;
import com.unla.agroecologiaiot.entities.Session;
import com.unla.agroecologiaiot.models.ApplicationUserModel;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.RoleRepository;
import com.unla.agroecologiaiot.repositories.SessionRepository;
import com.unla.agroecologiaiot.services.IApplicationUserService;
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

@Service("applicationUserService")
public class ApplicationUserService
  implements IApplicationUserService, UserDetailsService {

  private ModelMapper modelMapper = new ModelMapper();
  private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

  @Autowired
  @Qualifier("applicationUserRepository")
  private ApplicationUserRepository applicationUserRepository;

  @Autowired
  @Qualifier("roleRepository")
  private RoleRepository roleRepository;

  @Autowired
  @Qualifier("sessionRepository")
  private SessionRepository sessionRepository;

  @Override
  public long saveOrUpdate(ApplicationUserModel model) {
    ApplicationUser user = modelMapper.map(model, ApplicationUser.class);

    Role role = roleRepository.findById(model.getRoleId()).get();
    user.setEnabled(true);
    user.setRole(role);
    user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

    return applicationUserRepository.save(user).getUserId();
  }

  @Override
  public ApplicationUserModel getUser(long id) {
    Optional<ApplicationUser> dbUser = applicationUserRepository.findByIdAndFetchRoleEagerly(
      id
    );

    if (dbUser.isPresent()) {
      return modelMapper.map(dbUser.get(), ApplicationUserModel.class);
    }
    return null;
  }

  @Override
  public boolean logout(String token) {
    Optional<Session> session = sessionRepository.findByToken(token);
    if (session.isPresent()) {
      session.get().setActive(false);
      sessionRepository.save(session.get());
      return true;
    }

    return false;
  }

  public ApplicationUserModel getUser(String username) {
    Optional<ApplicationUser> dbUser = applicationUserRepository.findByUsernameAndFetchRoleEagerly(
      username
    );

    if (dbUser.isPresent()) {
      return modelMapper.map(dbUser.get(), ApplicationUserModel.class);
    }
    return null;
  }

  // Auth methods (from UserDetailsService)

  @Override
  public UserDetails loadUserByUsername(String username)
    throws UsernameNotFoundException {
    Optional<ApplicationUser> applicationUser = applicationUserRepository.findByUsername(
      username
    );

    if (!applicationUser.isPresent()) {
      throw new UsernameNotFoundException("Username not found");
    }

    return new User(
      applicationUser.get().getUsername(),
      applicationUser.get().getPassword(),
      applicationUser.get().isEnabled(),
      true,
      true,
      true,
      emptyList()
    );
  }
}
