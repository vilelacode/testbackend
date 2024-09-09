package com.vileladev.testbackend.entities.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@Validated
public class TransacaoRequest implements Serializable {

    @Digits(message = "Devem haver apenas dígitos.", integer = 10, fraction = 2)
    public Long numeroConta;


    @Min(value = 0, message = "O valor deve ser maior ou igual a zero")
    @Digits(message = "Devem haver apenas dígitos.", integer = 10, fraction = 2)
    public BigDecimal valor;


    public TransacaoRequest(Long numeroConta, BigDecimal valor) {
        this.numeroConta = numeroConta;
        this.valor = valor;
    }

}

