package com.vileladev.testbackend.services.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.vileladev.testbackend.entities.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;


    public String criarToken(Usuario usuario){
        return JWT.create()
                .withIssuer("baseApiApplication")
                .withSubject(usuario
                        .getUsername())
                .withClaim("id", usuario.getId())
                .withExpiresAt(LocalDateTime.now()
                        .plusMinutes(99)
                        .toInstant(ZoneOffset.of("-03:00"))
                ).sign(Algorithm.HMAC256(secret));
    }


    public String getSubject(String token) {
        return JWT.require(Algorithm.HMAC256(secret))
                .withIssuer("baseApiApplication")
                .build().verify(token).getSubject();

    }
}
