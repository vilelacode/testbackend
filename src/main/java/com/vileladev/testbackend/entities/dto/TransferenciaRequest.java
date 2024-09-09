package com.vileladev.testbackend.entities.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;
import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
public class TransferenciaRequest extends TransacaoRequest implements Serializable {
    private String tipoTransacao;

    public TransferenciaRequest(Long numeroConta, BigDecimal valor) {
        super(numeroConta, valor);
        this.tipoTransacao = "";
    }

    public TransferenciaRequest(Long numeroConta, BigDecimal valor, String tipoTransacao) {
        super(numeroConta, valor);
        this.tipoTransacao = tipoTransacao;
    }
}
