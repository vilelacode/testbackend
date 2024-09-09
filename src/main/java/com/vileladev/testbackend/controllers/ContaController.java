package com.vileladev.testbackend.controllers;

import com.vileladev.testbackend.entities.dto.CadastroRequest;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import com.vileladev.testbackend.services.ContaServiceImpl;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/conta")
public class ContaController {

    @Autowired
    private ContaServiceImpl contaService;


    @PostMapping("/criar")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> criarUsuarioEConta(@RequestBody @Validated CadastroRequest request) {

        return contaService.criarUsuarioEConta(request);
    }

    @PutMapping("/depositar")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> depositar(@RequestBody @Valid TransferenciaRequest request) {

        return contaService.depositar(request);
    }
    @PutMapping("/sacar/{valor}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> sacar(@PathVariable @Valid BigDecimal valor) {

        return contaService.sacar(valor);
    }

    @GetMapping("/saldo-extrato/consulta")
    public ResponseEntity<?> obterSaldoExtrato() {

        return ResponseEntity.ok(contaService.obterSaldoExtrato());
    }

    @PutMapping("/transferir")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> transferir(@RequestBody @Valid TransferenciaRequest request) {
        return contaService.transferir(request);
    }


}
