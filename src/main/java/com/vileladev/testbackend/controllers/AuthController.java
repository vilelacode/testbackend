package com.vileladev.testbackend.controllers;

import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.Login;

import com.vileladev.testbackend.repositories.UsuarioRepository;
import com.vileladev.testbackend.services.security.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody @Valid Login login) {
         UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
            new UsernamePasswordAuthenticationToken(login.login(), login.senha());

        if(usuarioRepository.findByLogin(login.login()) == null)
            return ResponseEntity.badRequest()
                    .body("Login não encontrado: " + login.login());

        Usuario usuario = (Usuario) this.authenticationManager
                .authenticate(usernamePasswordAuthenticationToken)
                .getPrincipal();

        return ResponseEntity.ok("Token: " + tokenService.criarToken(usuario));
    }


}

