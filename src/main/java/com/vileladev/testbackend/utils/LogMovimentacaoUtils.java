package com.vileladev.testbackend.utils;

import com.vileladev.testbackend.entities.Conta;
import com.vileladev.testbackend.entities.Extrato;
import com.vileladev.testbackend.entities.LogMovimentacao;
import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import com.vileladev.testbackend.repositories.ExtratoRepository;

import java.time.LocalDateTime;

public class LogMovimentacaoUtils {

    public static LogMovimentacao escreverLog(TransferenciaRequest request,
                                              Conta conta,
                                              Usuario usuario,
                                              ExtratoRepository extratoRepository
                                              ) {

        Extrato ex = extratoRepository.findByContaNumeroConta(conta.getNumeroConta())
                .orElseThrow(() ->
                        new RuntimeException("Houve um erro na validação de logs da conta."));
        return switch (request.getTipoTransacao()) {
            case "SAQUE" -> LogMovimentacao.builder()
                    .solicitante(conta.getNomeTitular())
                    .valor(request.getValor())
                    .dataHora(LocalDateTime.now())
                    .descricao("Saque")
                    .extrato(ex)
                    .build();
            case "TRANSFERENCIA ENVIADA" -> LogMovimentacao.builder()
                    .solicitante(usuario.getConta().getNomeTitular())
                    .numeroContaDestinatario(request.getNumeroConta())
                    .valor(request.getValor())
                    .dataHora(LocalDateTime.now())
                    .descricao("Transferência enviada")
                    .extrato(ex)
                    .build();
            case "TRANSFERENCIA RECEBIDA" -> LogMovimentacao.builder()
                    .solicitante(usuario.getConta().getNomeTitular())
                    .numeroContaDestinatario(conta.getNumeroConta())
                    .valor(request.getValor())
                    .dataHora(LocalDateTime.now())
                    .descricao("Transferência recebida")
                    .extrato(ex)
                    .build();
            default -> LogMovimentacao.builder()
                    .numeroContaDestinatario(request.getNumeroConta())
                    .solicitante(conta.getNomeTitular())
                    .valor(request.getValor())
                    .dataHora(LocalDateTime.now())
                    .descricao("Depósito")
                    .extrato(ex)
                    .build();
        };
    }
}
