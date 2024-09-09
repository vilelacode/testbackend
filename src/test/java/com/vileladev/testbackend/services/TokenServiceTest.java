package com.vileladev.testbackend.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.services.security.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private Usuario usuario;

    @BeforeEach
    void setUp() throws NoSuchFieldException, IllegalAccessException {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService();

        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, "chave");
    }

    @Test
    void testCriarToken() {
        when(usuario.getUsername()).thenReturn("usuarioTeste");
        when(usuario.getId()).thenReturn(1L);

        assertNotNull(tokenService.criarToken(usuario));
    }

    @Test
    void testGetSubject() {

        String token = JWT.create()
                .withIssuer("baseApiApplication")
                .withSubject("usuarioTeste")
                .sign(Algorithm.HMAC256("chave"));

        String subject = tokenService.getSubject(token);

        assertEquals("usuarioTeste", subject);
    }
}
