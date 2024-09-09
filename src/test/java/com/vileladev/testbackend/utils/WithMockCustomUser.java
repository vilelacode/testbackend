package com.vileladev.testbackend.utils;

import com.vileladev.testbackend.utils.CustomSecurityContextFactory;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = CustomSecurityContextFactory.class)
public @interface WithMockCustomUser {
    String username() default "user";
}
