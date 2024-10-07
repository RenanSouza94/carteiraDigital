package br.com.carteiradigital.domain.adapters;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.TipoTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

class TransacaoUseCaseImplTest {

    private TransacaoRepository transacaoRepository;
    private ContaRepository contaRepository;
    private LogUseCase log;
    private TransacaoUseCaseImpl transacaoUseCase;

    @BeforeEach
    void setUp() {
        transacaoRepository = mock(TransacaoRepository.class);
        contaRepository = mock(ContaRepository.class);
        log = mock(LogUseCase.class);
        transacaoUseCase = new TransacaoUseCaseImpl(transacaoRepository, log, contaRepository);
    }

    @Test
    void testEfetivarTransacaoRetirada_SaldoSuficiente() {
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.RETIRADA);
        transacao.setIdConta(UUID.randomUUID());
        transacao.setValor(new BigDecimal("100"));

        when(contaRepository.consultaSaldo(transacao.getIdConta())).thenReturn(new BigDecimal("150"));

        assertDoesNotThrow(() -> transacaoUseCase.efetivarTransacao(transacao));
        assertEquals(StatusTransacao.CONCLUIDA, transacao.getStatus());
        verify(contaRepository).atualizaSaldo(transacao.getValor().negate(), transacao.getIdConta());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void testEfetivarTransacaoRetirada_SaldoInsuficiente() {
        Transacao transacao = new Transacao();
        transacao.setTipo(TipoTransacao.RETIRADA);
        transacao.setIdConta(UUID.randomUUID());
        transacao.setValor(new BigDecimal("200"));

        when(contaRepository.consultaSaldo(transacao.getIdConta())).thenReturn(new BigDecimal("100"));

        assertDoesNotThrow(() -> transacaoUseCase.efetivarTransacao(transacao));
        assertEquals(StatusTransacao.FALHA, transacao.getStatus());
        assertEquals("Saldo insuficiente", transacao.getDescricaoStatus());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void testAdicionarTransacao_TransacaoPendente() {
        Transacao transacao = new Transacao();
        transacao.setIdentificador("123");

        when(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.PENDENTE))
                .thenReturn(false);

        Transacao resultado = transacaoUseCase.adicionarTransacao(transacao);

        assertEquals(StatusTransacao.PENDENTE, resultado.getStatus());
        assertEquals("Transação pendente de efetivação", resultado.getDescricaoStatus());
        assertNotNull(resultado.getDataHoraCriacao());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void testAdicionarTransacao_TransacaoExistente() {
        Transacao transacao = new Transacao();
        transacao.setIdentificador("123");

        when(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.PENDENTE))
                .thenReturn(true);

        assertThrows(TransacaoException.class, () -> transacaoUseCase.adicionarTransacao(transacao));
    }

    @Test
    void testCancelamentoTransacao_Pendente() {
        Transacao transacao = new Transacao();
        transacao.setIdentificador("123");
        transacao.setTipo(TipoTransacao.CANCELAMENTO);

        Transacao transacaoEncontrada = new Transacao();
        transacaoEncontrada.setIdentificador("123");
        transacaoEncontrada.setStatus(StatusTransacao.PENDENTE);

        when(transacaoRepository.findByIdentificadorAndStatus(transacao.getIdentificador(), StatusTransacao.PENDENTE))
                .thenReturn(Optional.of(transacaoEncontrada));

        assertDoesNotThrow(() -> transacaoUseCase.efetivarTransacao(transacao));
        assertEquals(StatusTransacao.CANCELADA, transacaoEncontrada.getStatus());
        verify(transacaoRepository).save(transacaoEncontrada);
    }

    @Test
    void testEstornoTransacao_Existente() {
        Transacao transacao = new Transacao();
        transacao.setIdentificador("123");
        transacao.setValor(new BigDecimal("100"));
        transacao.setIdConta(UUID.randomUUID());
        transacao.setTipo(TipoTransacao.ESTORNO);

        when(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.CONCLUIDA))
                .thenReturn(true);

        assertDoesNotThrow(() -> transacaoUseCase.efetivarTransacao(transacao));
        assertEquals(StatusTransacao.CONCLUIDA, transacao.getStatus());
        verify(contaRepository).atualizaSaldo(transacao.getValor(), transacao.getIdConta());
        verify(transacaoRepository).save(transacao);
    }

    @Test
    void testEstornoTransacao_NaoExistente() {
        Transacao transacao = new Transacao();
        transacao.setIdentificador("123");
        transacao.setValor(new BigDecimal("100"));
        transacao.setIdConta(UUID.randomUUID());
        transacao.setTipo(TipoTransacao.ESTORNO);

        when(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.CONCLUIDA))
                .thenReturn(false);

        assertDoesNotThrow(() -> transacaoUseCase.efetivarTransacao(transacao));
        assertEquals(StatusTransacao.FALHA, transacao.getStatus());
        assertEquals("Não foi encontrado a transação para estornar", transacao.getDescricaoStatus());
        verify(transacaoRepository).save(transacao);
    }

    // Adicione mais testes para cobrir outros cenários e métodos conforme necessário
}
