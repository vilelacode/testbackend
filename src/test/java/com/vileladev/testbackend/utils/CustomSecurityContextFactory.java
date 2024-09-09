package com.vileladev.testbackend.utils;

import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

class CustomSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser customUser) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        UserDetails userDetails = User.withUsername(customUser.username())
                .password("senha123")
                .roles("ADMIN")
                .build();

        TestingAuthenticationToken auth = new TestingAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        context.setAuthentication(auth);

        return context;
    }
}
