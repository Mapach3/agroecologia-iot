package com.unla.agroecologiaiot.services.implementation;

import static java.util.Collections.emptyList;

import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.entities.Role;
import com.unla.agroecologiaiot.entities.Session;
import com.unla.agroecologiaiot.helpers.MessageHelper.Message;
import com.unla.agroecologiaiot.models.ApplicationUserModel;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;
import com.unla.agroecologiaiot.repositories.RoleRepository;
import com.unla.agroecologiaiot.repositories.SessionRepository;
import com.unla.agroecologiaiot.services.IApplicationUserService;
import java.util.Optional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
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

  public ResponseEntity<String> saveOrUpdate(ApplicationUserModel model) {
    try {
      model.setUserId(0);
      ApplicationUser user = modelMapper.map(model, ApplicationUser.class);

      Role role = roleRepository.findById(model.getRoleId()).get();

      if (role == null) {
        return Message.ErrorSearchEntity();
      }

      user.setEnabled(true);
      user.setRole(role);
      user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

      long response = applicationUserRepository.save(user).getUserId();

      return Message.Ok(response);

    } catch (Exception e) {
      return Message.ErrorException();
    }
  }

  public ResponseEntity<String> put(ApplicationUserModel model, long id) {
    try {
      ApplicationUser user = applicationUserRepository.getById(id);
      Role role = roleRepository.findById(model.getRoleId()).get();

      if (user == null || role == null) {
        return Message.ErrorSearchEntity();
      }

      user.setEmail(model.getEmail());
      user.setName(model.getName());
      user.setSurname(model.getSurname());
      user.setRole(role);

      long response = applicationUserRepository.save(user).getUserId();

      return Message.Ok(response);

    } catch (Exception e) {
      return Message.ErrorException();
    }
  }

  public ResponseEntity<String> delete(long id) {
    try {
      applicationUserRepository.deleteById(id);
      return Message.Ok(true);

    } catch (Exception e) {
      return Message.ErrorException();
    }
  }


  public ResponseEntity<String> getById(long id) {
    try {
      Optional<ApplicationUser> dbUser = applicationUserRepository.findByIdAndFetchRoleEagerly(id);

      if (dbUser.isPresent()) {
        dbUser.get().setPassword(null);
        return Message.Ok(modelMapper.map(dbUser.get(), ApplicationUserModel.class));
      }

      return Message.ErrorSearchEntity();

    } catch (Exception e) {
      return Message.ErrorException();
    }
  }

  public ResponseEntity<String> logout(String token) {
    try {
      Optional<Session> session = sessionRepository.findByToken(token);

      if (session.isPresent()) {
        session.get().setActive(false);
        sessionRepository.save(session.get());
        return Message.Ok(true);
      }

      return Message.ErrorSearchEntity();

    } catch (Exception e) {
      return Message.ErrorException();
    }
  }

  public ResponseEntity<String> getByUsername(String username) {
    try {
      Optional<ApplicationUser> dbUser = applicationUserRepository.findByUsernameAndFetchRoleEagerly(username);

      if (dbUser.isPresent()) {
        dbUser.get().setPassword(null);
        return Message.Ok(modelMapper.map(dbUser.get(), ApplicationUserModel.class));
      }

      return Message.ErrorSearchEntity();
      
    } catch (Exception e) {
      return Message.ErrorException();
    }
  }

  // Auth methods (from UserDetailsService)

  @Override
  public UserDetails loadUserByUsername(String username)
      throws UsernameNotFoundException {
    Optional<ApplicationUser> applicationUser = applicationUserRepository.findByUsername(
        username);

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
        emptyList());
  }
}
