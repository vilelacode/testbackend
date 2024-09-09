package com.vileladev.testbackend.entities.dto;

import javax.validation.constraints.Pattern;

import jakarta.validation.constraints.*;

public record CadastroRequest(

        @Pattern(regexp = "\\d+", message = "O campo deve conter apenas números")
        @NotNull(message = "O número da conta não pode ser nulo")
        Long numeroConta,

        @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$", message = "Insira o nome num padrão de nome correto")
        @NotBlank(message = "O nome do titular não pode ser vazio")
        String nomeTitular,

        @NotBlank(message = "É preciso inserir um login")
        String login,

        @NotBlank(message = "É preciso inserir uma senha de no mínimo 4 caracteres")
        @Size(min = 4, message = "A senha deve ter no mínimo 4 caracteres")
        String senha
){

}
