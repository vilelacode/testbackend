package com.vileladev.testbackend.services;

import com.vileladev.testbackend.entities.Conta;
import com.vileladev.testbackend.entities.Extrato;
import com.vileladev.testbackend.entities.LogMovimentacao;
import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.CadastroRequest;
import com.vileladev.testbackend.entities.dto.SaldoExtratoResponse;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import com.vileladev.testbackend.enums.RoleEnum;
import com.vileladev.testbackend.repositories.ContaRepository;
import com.vileladev.testbackend.repositories.ExtratoRepository;
import com.vileladev.testbackend.repositories.LogMovimentacaoRepository;
import com.vileladev.testbackend.repositories.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.vileladev.testbackend.utils.Constants.*;
import static com.vileladev.testbackend.utils.ContaUtils.*;
import static com.vileladev.testbackend.utils.LogMovimentacaoUtils.escreverLog;

@Service
@AllArgsConstructor
public class ContaServiceImpl implements ContaService {

    private final ContaRepository contaRepository;
    private final ExtratoRepository extratoRepository;
    private final LogMovimentacaoRepository logMovimentacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public static final Logger log = LoggerFactory.getLogger(ContaServiceImpl.class);


    @Override
    public Usuario carregarDadosUsuario(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails user = (UserDetails) authentication.getPrincipal();
        return (Usuario) usuarioRepository.findByLogin(user.getUsername());
    }

    @Override
    @Transactional
    public ResponseEntity<String> criarUsuarioEConta(CadastroRequest request) {

        if (contaRepository.findByNumeroConta(request.numeroConta()).isPresent()) {
            return ResponseEntity.badRequest().body(ERRO_CONTA_JA_CRIADA);
        }

        if(usuarioRepository.findByLogin(request.login()) != null){
            return ResponseEntity.badRequest().body(ERRO_LOGIN_JA_CRIADO);
        }

        var novaConta = contaRepository.save(
                        new Conta(null,request.numeroConta(),
                                request.nomeTitular(),BigDecimal.ZERO));

        usuarioRepository.save(
                new Usuario(novaConta, request.login(),
                passwordEncoder.encode(request.senha()),
                RoleEnum.USER));

        contaRepository.flush();
        usuarioRepository.flush();

        extratoRepository.save(
                new Extrato(novaConta, Collections.emptyList()));

        return ResponseEntity.ok().body(CONTA_CRIADA_SUCESSO);
    }


    @Transactional
    @Override
    public ResponseEntity<String> transferir(TransferenciaRequest request)  {
        try {
            var usuario = carregarDadosUsuario();

            if (contaRepository.findByNumeroConta(request.getNumeroConta()).isEmpty()) {
                return ResponseEntity.badRequest().body(ERRO_CONTA_DESTINATARIO);
            }

            var contaDestinatario = getContaDestinatario(request, contaRepository);

            log.info("Iniciando a transação do valor de: R$ " + request.getValor() + " para a conta " + request.getNumeroConta());

            verificaTransferenciaEntreContasDistintas(contaDestinatario, usuario);

            try {
                verificaSaldoInsuficiente(request, usuario.getConta());

                logMovimentacaoRepository.save(
                        stepEnvioTransferencia(
                                request, usuario.getConta(), usuario, extratoRepository, contaRepository));
                logMovimentacaoRepository.save(
                        stepRecebimentoTransferencia(
                                request, contaDestinatario, usuario, extratoRepository, contaRepository));
                log.info("Transferido o valor de R$" + request.getValor() + " para a conta " + request.getNumeroConta());

                return ResponseEntity.ok().body(String.format(TRANSFERENCIA_REALIZADA_SUCESSO, request.getValor()));
            } catch (ResponseStatusException e) {
                log.error("Erro ao transferir o valor de R$" + request.getValor() + " para a conta " + request.getNumeroConta(), e);
                return ResponseEntity.badRequest().body(e.getReason());
            }
        } catch (RuntimeException e) {
            log.error("Erro ao processar a transferência: " + e.getMessage(), e);
            return ResponseEntity.badRequest().body(ERRO_TRANSFERENCIA);
        }
    }


    @Override
    @Transactional
    public ResponseEntity<String> depositar (TransferenciaRequest request) {

        try{

            if(contaRepository.findByNumeroConta(request.getNumeroConta()).isEmpty()){
                return ResponseEntity.badRequest().body(ERRO_CONTA_NAO_ENCONTRADA + request.getNumeroConta());
            }

            var usuario = carregarDadosUsuario();
            request.setTipoTransacao("DEPOSITO");

            log.info(" Iniciando a transação para a conta " + request.getNumeroConta());
            contaRepository.findByNumeroConta(request.getNumeroConta())
                    .ifPresent(conta -> {
                        contaRepository.updateSaldoByNumeroConta(conta.getSaldo()
                                .add(request.getValor()), request.getNumeroConta());
                        logMovimentacaoRepository.save(
                                escreverLog(
                                        request, conta, usuario, extratoRepository));
                        log.info(String.format(DEPOSITO_REALIZADO_SUCESSO, request.getValor(), request.getNumeroConta()));
                    });
                    return ResponseEntity.ok().body(String.format(DEPOSITO_REALIZADO_SUCESSO, request.getValor(), request.getNumeroConta()));

            } catch (Exception e) {
                log.error(ERRO_DEPOSITO);
                return ResponseEntity.badRequest().body(ERRO_DEPOSITO);
        }
    }

    @Override
    public SaldoExtratoResponse obterSaldoExtrato() {

        var usuario = carregarDadosUsuario();

        log.info("Obtendo saldo e extrato da conta " + usuario.getConta().getNumeroConta());

        Conta conta = contaRepository.findByNumeroConta(usuario.getConta().getNumeroConta())
                .orElseThrow(() -> new EntityNotFoundException(ERRO_CONTA));
        Extrato extrato = extratoRepository.findByContaNumeroConta(usuario.getConta().getNumeroConta())
                .orElseThrow(() ->
                        new EntityNotFoundException(SALDO_EXTRATO_NAO_ENCONTRADO));

        return new SaldoExtratoResponse(conta.getNumeroConta(), conta.getSaldo(),
                extrato.getLogs().stream().sorted(Comparator.comparing(LogMovimentacao::getDataHora).reversed())
                        .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    public ResponseEntity<String> sacar(BigDecimal valor) {
            try {
                var usuario = carregarDadosUsuario();
                log.info("Iniciando a transação de saque no valor de R$" + valor);
                Optional<Conta>
                        opConta = contaRepository.findByNumeroConta(
                                usuario.getConta().getNumeroConta());
                if(opConta.isEmpty()){
                    return ResponseEntity.badRequest().body(ERRO_CONTA_NAO_ENCONTRADA);
                }
                var conta = opConta.get();

                if (conta.getSaldo().compareTo(valor) < 0) {
                    log.error(SALDO_INSUFICIENTE);
                    return ResponseEntity.badRequest().body(SALDO_INSUFICIENTE);
                }

                var linhasAlteradas = contaRepository.updateSaldoByNumeroConta(
                        conta.getSaldo().subtract(valor),
                        conta.getNumeroConta());

                if (linhasAlteradas > 0) {
                    var logMovimentacao = escreverLog(
                            new TransferenciaRequest(conta.getNumeroConta(), valor, "SAQUE"),
                            conta, usuario, extratoRepository);
                    logMovimentacaoRepository.save(logMovimentacao);
                } else {
                    log.error(String.format(ERRO_SAQUE,valor));
                    throw new RuntimeException(String.format(ERRO_SAQUE,valor));
                    }
                return ResponseEntity.ok().body(String.format(SAQUE_REALIZADO_SUCESSO, valor));

        } catch (RuntimeException e) {
            log.error(ERRO_SAQUE);
            return ResponseEntity.badRequest().body(ERRO_SAQUE);
        }
    }
}
