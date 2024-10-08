package br.com.carteiradigital.application.rest.controller;

import br.com.carteiradigital.domain.exception.ContaException;
import br.com.carteiradigital.domain.port.usecase.ContaUseCase;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaControllerTest {

    @Mock
    private ContaUseCase contaUseCase;

    @Mock
    private LogUseCase logUseCase;

    @InjectMocks
    private ContaController contaController;

    private UUID idConta;

    @BeforeEach
    void setUp() {
        idConta = UUID.randomUUID();
    }

    @Test
    void deveRetornarSaldoComSucesso() {
        BigDecimal saldoEsperado = new BigDecimal(100);
        when(contaUseCase.consultaSaldo(idConta)).thenReturn(saldoEsperado);

        ResponseEntity<?> response = contaController.calcularSaldo(idConta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(saldoEsperado, response.getBody());
        verify(contaUseCase, times(1)).consultaSaldo(idConta); // Verifica se o método foi chamado uma vez
    }

    @Test
    void deveRetornarBadRequestQuandoContaNaoExistir() {
        when(contaUseCase.consultaSaldo(idConta)).thenThrow(new ContaException("Conta não encontrada"));

        ResponseEntity<?> response = contaController.calcularSaldo(idConta);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Conta não encontrada", response.getBody());
        verify(contaUseCase, times(1)).consultaSaldo(idConta);
    }

    @Test
    void deveRetornarErroInternoQuandoOcorreExcecaoGenerica() {

        when(contaUseCase.consultaSaldo(idConta)).thenThrow(new RuntimeException("Erro inesperado"));

        ResponseEntity<?> response = contaController.calcularSaldo(idConta);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro ao calcular Saldo", response.getBody());
        verify(logUseCase, times(1)).error("Erro:calcularSaldo Erro inesperado");
    }
}
