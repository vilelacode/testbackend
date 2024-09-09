package com.vileladev.testbackend.services.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.vileladev.testbackend.repositories.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltroToken extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token;

            var authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader != null) {
                token = authorizationHeader.replace("Bearer ", "");
                var subject = this.tokenService.getSubject(token);

                var usuario = this.usuarioRepository.findByLogin(subject);

                var authentication = new UsernamePasswordAuthenticationToken(usuario,
                        null, usuario.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            filterChain.doFilter(request, response);

        } catch (TokenExpiredException ex) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            response.getWriter().flush();
        }
    }
}
