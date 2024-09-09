package com.vileladev.testbackend.utils;

import com.vileladev.testbackend.entities.Conta;
import com.vileladev.testbackend.entities.LogMovimentacao;
import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import com.vileladev.testbackend.repositories.ContaRepository;
import com.vileladev.testbackend.repositories.ExtratoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

import static com.vileladev.testbackend.services.ContaServiceImpl.log;
import static com.vileladev.testbackend.utils.Constants.*;
import static com.vileladev.testbackend.utils.LogMovimentacaoUtils.escreverLog;

public class ContaUtils {

    public static Conta getContaDestinatario(TransferenciaRequest request,
                                       ContaRepository contaRepository) {
        return contaRepository.findByNumeroConta(request.getNumeroConta()).orElseThrow(() -> {
            log.error(ERRO_CONTA);
            return new ResponseStatusException(HttpStatus.BAD_REQUEST, ERRO_CONTA);
        });
    }

    public static void verificaTransferenciaEntreContasDistintas(Conta contaDestinatario, Usuario usuario) {
        if (Objects.equals(contaDestinatario.getNumeroConta(), usuario.getConta().getNumeroConta())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, TRASFERENCIA_ENTRE_CONTAS_DISTINTAS);
        }
    }

    public static void verificaSaldoInsuficiente(TransferenciaRequest request, Conta conta) {
        if (conta.getSaldo().compareTo(request.getValor()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, SALDO_INSUFICIENTE);
        }
    }

    public static LogMovimentacao stepRecebimentoTransferencia(TransferenciaRequest request,
                                                        Conta contaDestinatario,
                                                        Usuario usuario,
                                                        ExtratoRepository extratoRepository,
                                                        ContaRepository contaRepository) {
        request.setTipoTransacao("TRANSFERENCIA RECEBIDA");
        LogMovimentacao logRecebimento = escreverLog(request, contaDestinatario, usuario, extratoRepository);

        contaRepository.updateSaldoByNumeroConta(
                contaDestinatario.getSaldo().add(request.getValor()), request.getNumeroConta());
        return logRecebimento;
    }

    public static LogMovimentacao stepEnvioTransferencia(TransferenciaRequest request,
                                                  Conta contaRemetente,
                                                  Usuario usuario,
                                                  ExtratoRepository extratoRepository,
                                                  ContaRepository contaRepository) {
        request.setTipoTransacao("TRANSFERENCIA ENVIADA");
        LogMovimentacao logEnvio = escreverLog(request, contaRemetente, usuario, extratoRepository);
        contaRepository.updateSaldoByNumeroConta(
                contaRemetente.getSaldo().subtract(request.getValor()), usuario.getConta().getNumeroConta());
        return logEnvio;
    }

}
