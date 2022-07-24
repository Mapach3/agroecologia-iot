package com.unla.agroecologiaiot.filters;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.unla.agroecologiaiot.constants.Constants;
import com.unla.agroecologiaiot.constants.SecurityConstants;
import com.unla.agroecologiaiot.entities.ApplicationUser;
import com.unla.agroecologiaiot.models.auth.LoginDTO;
import com.unla.agroecologiaiot.models.auth.LoginResponse;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;
import com.unla.agroecologiaiot.repositories.ApplicationUserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.User;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private Gson gson = new Gson();

    private ApplicationUserRepository applicationUserRepository;

    public AuthenticationFilter(AuthenticationManager authenticationManager,
            ApplicationUserRepository applicationUserRepository) {
        this.authenticationManager = authenticationManager;
        this.applicationUserRepository = applicationUserRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req, HttpServletResponse res)
            throws AuthenticationException {

        try {
            LoginDTO user = new ObjectMapper().readValue(req.getInputStream(), LoginDTO.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), new ArrayList()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req, HttpServletResponse res, FilterChain chain,
            Authentication auth) throws IOException, ServletException {

        Date exp = new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME);
        Key key = Keys.hmacShaKeyFor(SecurityConstants.SECRET.getBytes());

        ApplicationUser validatedUser = applicationUserRepository
                .findByUsernameAndFetchRoleEagerly(((User) auth.getPrincipal()).getUsername()).get();

        Map<String, Object> customClaims = new HashMap<String, Object>();
        customClaims.put(SecurityConstants.CustomSecurityClaims.ROLE, validatedUser.getRole().getCode());
        customClaims.put(SecurityConstants.CustomSecurityClaims.EMAIL, validatedUser.getEmail());
        customClaims.put(SecurityConstants.CustomSecurityClaims.USERNAME, validatedUser.getUsername());

        Claims claims = Jwts.claims(customClaims);

        // Build JWT Token with custom claims
        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(Long.toString(validatedUser.getUserId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();

        ProfileDTO profile = ProfileDTO.builder().username(validatedUser.getUsername()).name(validatedUser.getName())
                .surname(validatedUser.getSurname()).roleCode(validatedUser.getRole().getCode()).build();

        // Create response which will be stored in Web App
        LoginResponse response = new LoginResponse(token, profile);
        String jsonResponse = this.gson.toJson(response);

        // Set custom servlet response
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(Constants.ContentTypes.APPLICATION_JSON);
        res.getWriter().print(jsonResponse);
        res.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException e) throws IOException, ServletException {

        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.setContentType(Constants.ContentTypes.APPLICATION_JSON);
        res.getWriter().print(this.gson.toJson("Revise sus credenciales"));
        res.getWriter().flush();
    }
}
