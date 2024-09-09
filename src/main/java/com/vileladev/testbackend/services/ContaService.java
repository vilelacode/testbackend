package com.vileladev.testbackend.services;

import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.CadastroRequest;
import com.vileladev.testbackend.entities.dto.SaldoExtratoResponse;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.math.BigDecimal;

public interface ContaService {

    ResponseEntity<String> depositar (TransferenciaRequest request);

    SaldoExtratoResponse obterSaldoExtrato();

    ResponseEntity<String> sacar(BigDecimal valor);

    Usuario carregarDadosUsuario();

    ResponseEntity<String> criarUsuarioEConta(CadastroRequest request);

    ResponseEntity<String>  transferir(@Valid TransferenciaRequest request);
}
