package com.unla.agroecologiaiot.configuration;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuditorAwareImpl implements AuditorAware<Long>{
                
        @Override
        @SuppressWarnings("unchecked")
        public Optional<Long> getCurrentAuditor() {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (authentication == null || !authentication.isAuthenticated()) {
                 return null;
                }

                // String claims =  SecurityContextHolder.getContext().getAuthentication().getName();
                
                var principal = ((Map<String, String>) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

                // var sub = claims.split("sub=")[1].split(",")[0].toString();

                Optional<Long> userId = Optional.of(Long.parseLong(principal.get("sub")));

                return userId;
        }
}
