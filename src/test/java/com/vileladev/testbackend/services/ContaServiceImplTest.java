package com.vileladev.testbackend.services;

import com.vileladev.testbackend.entities.Conta;
import com.vileladev.testbackend.entities.Extrato;
import com.vileladev.testbackend.entities.LogMovimentacao;
import com.vileladev.testbackend.entities.Usuario;
import com.vileladev.testbackend.entities.dto.CadastroRequest;
import com.vileladev.testbackend.entities.dto.TransferenciaRequest;
import com.vileladev.testbackend.repositories.ContaRepository;
import com.vileladev.testbackend.repositories.ExtratoRepository;
import com.vileladev.testbackend.repositories.LogMovimentacaoRepository;
import com.vileladev.testbackend.repositories.UsuarioRepository;
import com.vileladev.testbackend.utils.Constants;
import com.vileladev.testbackend.utils.WithMockCustomUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.vileladev.testbackend.utils.Constants.*;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class ContaServiceImplTest {

    @InjectMocks
    private ContaServiceImpl contaService;

    @Mock
    private ContaRepository contaRepositoryMock;

    @Mock
    private UsuarioRepository usuarioRepositoryMock;

    @Mock
    private PasswordEncoder passwordEncoderMock;

    @Mock
    private ExtratoRepository extratoRepositoryMock;

    @Mock
    private LogMovimentacaoRepository logMovimentacaoRepositoryMock;

    private Usuario usuarioAdminMock;

    private CadastroRequest request;

    @BeforeEach
    void setup() {
        usuarioAdminMock =obterUsuarioAdminMock();
        request = obterUsuarioCadastroRequest();
    }

    @Test
    @WithMockUser(username = "joao", roles = {"USER"})
    public void testeCriarUsuarioEContaPorMock() throws Exception {

        String login = "joao";
        String senha = "senha123";
        Long numeroConta = 101010L;
        String nomeTitular = "Usuário Teste";
        CadastroRequest request = new CadastroRequest(numeroConta, login, senha, nomeTitular);

        Mockito.when(passwordEncoderMock.encode(senha)).thenReturn("senhaemhash");

        Mockito.when(usuarioRepositoryMock.findByLogin(login)).thenReturn(null);
        Mockito.when(contaRepositoryMock.findByNumeroConta(numeroConta)).thenReturn(Optional.empty());
        Mockito.when(usuarioRepositoryMock.save(any(Usuario.class))).thenReturn(new Usuario());
        Mockito.when(contaRepositoryMock.save(any(Conta.class))).thenReturn(new Conta());
        Mockito.when(extratoRepositoryMock.save(any(Extrato.class))).thenReturn(new Extrato());

        ResponseEntity<String> response = contaService.criarUsuarioEConta(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(CONTA_CRIADA_SUCESSO, response.getBody());

    }

    @Test
    @WithMockUser(username = "joao", roles = {"USER"})
    public void esperadaExcessaoCasoHajaInclusaoDeUsuarioJaExistente() throws Exception {

        String login = "joao";
        String senha = "senha123";
        Long numeroConta = 101010L;
        String nomeTitular = "Usuário Teste";
        CadastroRequest request = new CadastroRequest(numeroConta, login, senha, nomeTitular);

        Mockito.when(usuarioRepositoryMock.findByLogin(request.login())).thenReturn(new Usuario());

        ResponseEntity<String> response = contaService.criarUsuarioEConta(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ERRO_LOGIN_JA_CRIADO, response.getBody());
    }

    @Test
    public void esperadaExcessaoCasoHajaInclusaoDeContaJaExistente() throws Exception {

        CadastroRequest request = obterUsuarioCadastroRequest();

        Mockito.when(usuarioRepositoryMock.findByLogin(request.login())).thenReturn(null);
        Mockito.when(contaRepositoryMock.findByNumeroConta(request.numeroConta())).thenReturn(Optional.of(new Conta()));

        ResponseEntity<String> response = contaService.criarUsuarioEConta(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(Constants.ERRO_CONTA_JA_CRIADA, response.getBody());
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoSucessoAoDepositar() throws Exception {

        Conta conta = new Conta(1L,101010L, "admin", BigDecimal.ZERO);
       when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(extratoRepositoryMock.findByContaNumeroConta(anyLong()))
                .thenReturn(Optional.of(
                        new Extrato(conta, List.of(LogMovimentacao.builder().build()))));
        when(logMovimentacaoRepositoryMock.save(any(LogMovimentacao.class))).thenReturn(
                new LogMovimentacao(1L, LocalDateTime.now(), "admin",
                        BigDecimal.TEN, "Saque", new Extrato(conta, emptyList())
                ));


        var response = contaService.depositar(
                new TransferenciaRequest(request.numeroConta(), BigDecimal.TEN));


        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(String.format(DEPOSITO_REALIZADO_SUCESSO, BigDecimal.TEN, request.numeroConta()), response.getBody());
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoExcessaoAoDepositarContaNaoEncontrada() throws Exception {

        Conta conta = new Conta(1L,101010L, "admin", BigDecimal.ZERO);
        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.empty());

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());

        var response = contaService.depositar(
                new TransferenciaRequest(request.numeroConta(), BigDecimal.TEN));


        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains(ERRO_CONTA_NAO_ENCONTRADA));
    }



    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoObterSaldoExtrato(){
        Conta conta = new Conta(1L,101010L, "admin", BigDecimal.ZERO);

        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));
        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(extratoRepositoryMock.findByContaNumeroConta(anyLong()))
                .thenReturn(Optional.of(
                        new Extrato(conta, List.of(LogMovimentacao.builder().build()))));

        assertEquals(BigDecimal.ZERO, contaService.obterSaldoExtrato().saldoTotal());
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoExcessaoObterSaldoExtrato(){
        Conta conta = new Conta(1L,101010L, "admin", BigDecimal.ZERO);

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(extratoRepositoryMock.findByContaNumeroConta(anyLong()))
                .thenReturn(Optional.of(
                        new Extrato(conta, List.of(LogMovimentacao.builder().build()))));
        var exception =  assertThrows(RuntimeException.class, () -> contaService.obterSaldoExtrato());

        assertEquals("Conta não encontrada.",exception.getMessage());
    }


    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoSucessoAoSacar() throws Exception {


        Conta conta = new Conta(1L,request.numeroConta(), "admin", BigDecimal.TEN);
        List<LogMovimentacao> listaLog = emptyList();

        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));
        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(contaRepositoryMock.updateSaldoByNumeroConta(
                conta.getSaldo().subtract(BigDecimal.TEN),
                conta.getNumeroConta())).thenReturn(1);
        when(extratoRepositoryMock.findByContaNumeroConta(conta.getNumeroConta()))
                .thenReturn(Optional.of(new Extrato(conta, listaLog)));
        when(logMovimentacaoRepositoryMock.save(any(LogMovimentacao.class))).thenReturn(
                new LogMovimentacao(1L, LocalDateTime.now(), "admin",
                        BigDecimal.TEN, "Saque", new Extrato(conta, listaLog)
        ));

        assertDoesNotThrow(() -> contaService.sacar(BigDecimal.TEN));
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoExcessaoAoSacarNaoAchandoConta() {

        Conta conta = new Conta(1L,request.numeroConta(), "admin", BigDecimal.TEN);
        List<LogMovimentacao> listaLog = emptyList();

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(extratoRepositoryMock.findByContaNumeroConta(conta.getNumeroConta()))
                .thenReturn(Optional.of(new Extrato(conta, listaLog)));

        assertEquals(ResponseEntity.badRequest().body(ERRO_CONTA_NAO_ENCONTRADA), contaService.sacar(BigDecimal.TEN));
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoExcessaoAoSacarSaldoInsuficiente() {

        Conta conta = new Conta(1L,request.numeroConta(), "admin", BigDecimal.TEN);
        List<LogMovimentacao> listaLog = emptyList();

        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));
        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(extratoRepositoryMock.findByContaNumeroConta(conta.getNumeroConta()))
                .thenReturn(Optional.of(new Extrato(conta, listaLog)));

        assertEquals(ResponseEntity.badRequest().body(SALDO_INSUFICIENTE), contaService.sacar(BigDecimal.valueOf(20)));
    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoErroAoSacarLogMovimentacao() throws Exception {

        Conta conta = new Conta(1L,request.numeroConta(), "admin", BigDecimal.TEN);
        List<LogMovimentacao> listaLog = emptyList();

        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));
        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(obterUsuarioAdminMock());
        when(contaRepositoryMock.updateSaldoByNumeroConta(
                conta.getSaldo().subtract(BigDecimal.TEN),
                conta.getNumeroConta())).thenReturn(1);

        assertEquals(ResponseEntity.badRequest().body(ERRO_SAQUE), contaService.sacar(BigDecimal.TEN));

    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoResponseExceptionAoTransferir() throws Exception {

        Long numeroConta = 101010L;

        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        Conta conta = new Conta(1L, numeroConta, "admin", BigDecimal.TEN);
        usuario.setConta(conta);

        ResponseEntity<String> response = contaService.criarUsuarioEConta(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(usuario);
        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta));

        var resultado = contaService.transferir(new TransferenciaRequest(request.numeroConta(), BigDecimal.TEN));

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());

    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoSucessoAoTransferir(){


        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        Conta conta = new Conta(1L, 101010L, "admin", BigDecimal.TEN);
        usuario.setConta(conta);
        Conta conta2 = new Conta(2L, 202020L, "admin", BigDecimal.TEN);

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(usuario);
        when(contaRepositoryMock.findByNumeroConta(anyLong())).thenReturn(Optional.of(conta2));
        when(extratoRepositoryMock.findByContaNumeroConta(any())).thenReturn(Optional.of(new Extrato(conta, emptyList())));
        when(contaRepositoryMock.updateSaldoByNumeroConta(any(),any())).thenReturn(1);

        var resultado = contaService.transferir(new TransferenciaRequest(101010L, BigDecimal.TEN));

        assertEquals(HttpStatus.OK, resultado.getStatusCode());

    }

    @Test
    @WithMockCustomUser(username = "admin")
    public void esperadoExcessaoAoTransferirContaNaoEncontrada(){


        Usuario usuario = new Usuario();
        usuario.setLogin("admin");
        Conta conta = new Conta(1L, 101010L, "admin", BigDecimal.TEN);
        usuario.setConta(conta);
        Conta conta2 = new Conta(2L, 202020L, "admin", BigDecimal.TEN);

        when(usuarioRepositoryMock.findByLogin(anyString())).thenReturn(usuario);
        when(extratoRepositoryMock.findByContaNumeroConta(any())).thenReturn(Optional.of(new Extrato(conta, emptyList())));
        when(contaRepositoryMock.updateSaldoByNumeroConta(any(),any())).thenReturn(1);

        var resultado = contaService.transferir(new TransferenciaRequest(101010L, BigDecimal.TEN));

        assertEquals(HttpStatus.BAD_REQUEST, resultado.getStatusCode());

    }


    private Usuario obterUsuarioAdminMock() {
        String login = "admin";
        String senha = "senha123";
        Long numeroConta2 = 202020L;
        String nomeTitular = "usuario Teste";
        CadastroRequest request = new CadastroRequest(numeroConta2, login, senha, nomeTitular);

        Usuario usuario = new Usuario();
        usuario.setLogin(request.login());
        usuario.setSenha(request.senha());

        Conta conta = new Conta();
        conta.setNumeroConta(request.numeroConta());
        conta.setNomeTitular(request.nomeTitular());

        usuario.setConta(conta);
        return usuario;
    }

    private CadastroRequest obterUsuarioCadastroRequest() {
        String login = "admin";
        String senha = "senha123";
        Long numeroConta2 = 202020L;
        String nomeTitular = "usuario Teste";

        return new CadastroRequest(numeroConta2, login, senha, nomeTitular);
    }


}
