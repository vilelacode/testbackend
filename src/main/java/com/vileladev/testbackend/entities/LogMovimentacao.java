package com.vileladev.testbackend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "logs_movimentacao")
@Data
@Builder
public class LogMovimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime dataHora;
    private String solicitante;
    private Long numeroContaDestinatario;
    private BigDecimal valor;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "extrato_id")
    @JsonIgnore
    private Extrato extrato;

    public LogMovimentacao(Long id, LocalDateTime dataHora, String solicitante,
                           Long numeroContaDestinatario, BigDecimal valor,
                           String descricao, Extrato extrato) {
        this.id = id;
        this.dataHora = dataHora;
        this.solicitante = solicitante;
        this.numeroContaDestinatario = numeroContaDestinatario;
        this.valor = valor;
        this.descricao = descricao;
        this.extrato = extrato;
    }

    public LogMovimentacao(Long id, LocalDateTime dataHora, String solicitante,
                           BigDecimal valor, String descricao, Extrato extrato) {
        this.id = id;
        this.dataHora = dataHora;
        this.solicitante = solicitante;
        this.valor = valor;
        this.descricao = descricao;
        this.extrato = extrato;
    }

    public LogMovimentacao() {
        this.dataHora = LocalDateTime.now();
    }
}
