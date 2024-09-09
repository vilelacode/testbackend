package com.vileladev.testbackend.entities.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Login(
        @NotBlank(message = "Insira o login.")
        @Size(min = 4, max = 50)
        String login,
        @NotBlank (message = "Insira sua senha.")
        String senha
) {
}
