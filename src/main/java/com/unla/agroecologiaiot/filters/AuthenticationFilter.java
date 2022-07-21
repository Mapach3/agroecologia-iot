package com.unla.agroecologiaiot.filters;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

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
import com.unla.agroecologiaiot.models.auth.LoginDTO;
import com.unla.agroecologiaiot.models.auth.LoginResponse;
import com.unla.agroecologiaiot.models.auth.ProfileDTO;
import com.unla.agroecologiaiot.services.IApplicationUserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.User;

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private IApplicationUserService applicationUserDetailsService;
    private Gson gson = new Gson();

    public AuthenticationFilter(AuthenticationManager authenticationManager,
            IApplicationUserService applicationUserDetailsService) {
        this.authenticationManager = authenticationManager;
        this.applicationUserDetailsService = applicationUserDetailsService;
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

        // Get profile to store in Web App
        ProfileDTO profile = applicationUserDetailsService.getProfile(((User) auth.getPrincipal()).getUsername());

        // TODO: Create a custom Claim with user Information, so we use it later to set
        // the SecurityContextHolder.
        Claims claims = Jwts
                .claims()
                .setSubject(this.gson.toJson(profile));

        // Build JWT Token
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(exp)
                .compact();

        LoginResponse response = new LoginResponse(token, profile);
        String jsonResponse = this.gson.toJson(response);

        // Set custom response
        res.setStatus(HttpServletResponse.SC_OK);
        res.setContentType(Constants.ContentTypes.APPLICATION_JSON);
        res.getWriter().print(jsonResponse);
        res.getWriter().flush();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest req, HttpServletResponse res,
            AuthenticationException e) throws IOException, ServletException {

        res.getWriter().write("Credenciales inválidas");
        res.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        res.getWriter().flush();

    }
}
