package com.unla.agroecologiaiot.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.unla.agroecologiaiot.entities.ApplicationUser;

@Repository("applicationUserRepository")
public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    public abstract Optional<ApplicationUser> findByUsername(String username);

    @Query("SELECT u FROM ApplicationUser u JOIN FETCH u.role where u.userId = (:id)")
    public abstract Optional<ApplicationUser> findByIdAndFetchRoleEagerly(@Param("id") long id);

    @Query("SELECT u FROM ApplicationUser u JOIN FETCH u.role where u.username = (:username)")
    public abstract Optional<ApplicationUser> findByUsernameAndFetchRoleEagerly(@Param("username") String username);

    @Query("SELECT u FROM ApplicationUser u JOIN FETCH u.role r where r.roleId = (:id)")
    public abstract List<ApplicationUser> findAllUsersByRoleId(@Param("id") long id);

}
