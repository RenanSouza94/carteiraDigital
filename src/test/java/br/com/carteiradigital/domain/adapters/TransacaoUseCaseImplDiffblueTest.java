package br.com.carteiradigital.domain.adapters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.carteiradigital.domain.entity.EfetivaTransacao;
import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.TipoTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.infrastructure.repository.impl.H2ContaRepositoryImpl;
import br.com.carteiradigital.infrastructure.repository.impl.H2TransacaoRepositoryImpl;
import br.com.carteiradigital.infrastructure.service.LogUseCaseImpl;

import java.math.BigDecimal;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {TransacaoUseCaseImpl.class})
@ExtendWith(SpringExtension.class)
@DisabledInAotMode
class TransacaoUseCaseImplDiffblueTest {
    @MockBean
    private ContaRepository contaRepository;

    @MockBean
    private LogUseCase logUseCase;

    @MockBean
    private TransacaoRepository transacaoRepository;

    @Autowired
    private TransacaoUseCaseImpl transacaoUseCaseImpl;

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao() throws TransacaoException {
        // Arrange
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        Optional<Transacao> ofResult = Optional.of(new Transacao());
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);

        // Act
        transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", true));

        // Assert
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao2() throws TransacaoException {
        // Arrange
        doThrow(new TransacaoException("An error occurred")).when(transacaoRepository).save(Mockito.<Transacao>any());
        Optional<Transacao> ofResult = Optional.of(new Transacao());
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", true)));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao3() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        doNothing().when(transacao).cancelar();
        Optional<Transacao> ofResult = Optional.of(transacao);
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);

        // Act
        transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", true));

        // Assert that nothing has changed
        verify(transacao).cancelar();
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao4() throws TransacaoException {
        // Arrange
        Optional<Transacao> emptyResult = Optional.empty();
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(emptyResult);

        // Act
        transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", true));

        // Assert that nothing has changed
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao5() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getIdentificador()).thenReturn("Identificador");
        doNothing().when(transacao).setDescricaoStatus(Mockito.<String>any());
        doNothing().when(transacao).setStatus(Mockito.<StatusTransacao>any());
        when(transacao.getTipo()).thenReturn(TipoTransacao.ADICAO);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(true);
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);

        // Act
        transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false));

        // Assert that nothing has changed
        verify(transacao).getIdentificador();
        verify(transacao).getTipo();
        verify(transacao).setDescricaoStatus(eq("Transação em duplicidade"));
        verify(transacao).setStatus(eq(StatusTransacao.FALHA));
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(eq("Identificador"),
                eq(StatusTransacao.CONCLUIDA));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao6() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getIdentificador()).thenThrow(new TransacaoException("An error occurred"));
        when(transacao.getTipo()).thenReturn(TipoTransacao.ADICAO);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(logUseCase).error(Mockito.<String>any());

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao).getIdentificador();
        verify(transacao).getTipo();
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicao: An error occurred"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao7() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getIdentificador()).thenThrow(new TransacaoException("An error occurred"));
        when(transacao.getTipo()).thenReturn(TipoTransacao.ADICAO);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doThrow(new TransacaoException("An error occurred")).when(logUseCase).error(Mockito.<String>any());

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao).getIdentificador();
        verify(transacao).getTipo();
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicao: An error occurred"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao8() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        doNothing().when(transacao).concluir();
        when(transacao.getValor()).thenReturn(new BigDecimal("2.3"));
        when(transacao.getIdConta()).thenReturn(UUID.randomUUID());
        when(transacao.getTipo()).thenReturn(TipoTransacao.RETIRADA);
        Optional<Transacao> ofResult = Optional.of(transacao);
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(contaRepository).atualizaSaldo(Mockito.<BigDecimal>any(), Mockito.<UUID>any());
        when(contaRepository.consultaSaldo(Mockito.<UUID>any())).thenReturn(new BigDecimal("2.3"));

        // Act
        transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false));

        // Assert that nothing has changed
        verify(transacao).concluir();
        verify(transacao, atLeast(1)).getIdConta();
        verify(transacao).getTipo();
        verify(transacao, atLeast(1)).getValor();
        verify(contaRepository).atualizaSaldo(isA(BigDecimal.class), isA(UUID.class));
        verify(contaRepository).consultaSaldo(isA(UUID.class));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao9() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getValor()).thenReturn(new BigDecimal("2.3"));
        when(transacao.getIdConta()).thenReturn(UUID.randomUUID());
        doThrow(new TransacaoException("An error occurred")).when(transacao).setStatus(Mockito.<StatusTransacao>any());
        when(transacao.getTipo()).thenReturn(TipoTransacao.RETIRADA);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(logUseCase).error(Mockito.<String>any());
        doThrow(new TransacaoException("An error occurred")).when(contaRepository)
                .atualizaSaldo(Mockito.<BigDecimal>any(), Mockito.<UUID>any());
        when(contaRepository.consultaSaldo(Mockito.<UUID>any())).thenReturn(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao, atLeast(1)).getIdConta();
        verify(transacao).getTipo();
        verify(transacao, atLeast(1)).getValor();
        verify(transacao).setStatus(eq(StatusTransacao.FALHA));
        verify(contaRepository).atualizaSaldo(isA(BigDecimal.class), isA(UUID.class));
        verify(contaRepository).consultaSaldo(isA(UUID.class));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:retirada An error occurred"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao10() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getValor()).thenReturn(new BigDecimal("4.5"));
        when(transacao.getIdConta()).thenReturn(UUID.randomUUID());
        doThrow(new TransacaoException("An error occurred")).when(transacao).setStatus(Mockito.<StatusTransacao>any());
        when(transacao.getTipo()).thenReturn(TipoTransacao.RETIRADA);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(logUseCase).error(Mockito.<String>any());
        when(contaRepository.consultaSaldo(Mockito.<UUID>any())).thenReturn(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao).getIdConta();
        verify(transacao).getTipo();
        verify(transacao).getValor();
        verify(transacao, atLeast(1)).setStatus(eq(StatusTransacao.FALHA));
        verify(contaRepository).consultaSaldo(isA(UUID.class));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:retirada An error occurred"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao11() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getValor()).thenReturn(null);
        when(transacao.getIdConta()).thenReturn(UUID.randomUUID());
        doThrow(new TransacaoException("An error occurred")).when(transacao).setStatus(Mockito.<StatusTransacao>any());
        when(transacao.getTipo()).thenReturn(TipoTransacao.RETIRADA);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(logUseCase).error(Mockito.<String>any());
        when(contaRepository.consultaSaldo(Mockito.<UUID>any())).thenReturn(new BigDecimal("2.3"));

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao).getIdConta();
        verify(transacao).getTipo();
        verify(transacao).getValor();
        verify(transacao).setStatus(eq(StatusTransacao.FALHA));
        verify(contaRepository).consultaSaldo(isA(UUID.class));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:retirada Cannot read field \"scale\" because \"val\" is null"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#efetivarTransacao(EfetivaTransacao)}
     */
    @Test
    void testEfetivarTransacao12() throws TransacaoException {
        // Arrange
        Transacao transacao = mock(Transacao.class);
        when(transacao.getIdentificador()).thenThrow(new TransacaoException("An error occurred"));
        doThrow(new TransacaoException("An error occurred")).when(transacao).setStatus(Mockito.<StatusTransacao>any());
        when(transacao.getTipo()).thenReturn(TipoTransacao.ESTORNO);
        Optional<Transacao> ofResult = Optional.of(transacao);
        when(transacaoRepository.findByIdentificadorAndStatus(Mockito.<String>any(), Mockito.<StatusTransacao>any()))
                .thenReturn(ofResult);
        doNothing().when(logUseCase).error(Mockito.<String>any());

        // Act and Assert
        assertThrows(TransacaoException.class,
                () -> transacaoUseCaseImpl.efetivarTransacao(new EfetivaTransacao("Identificacao", false)));
        verify(transacao).getIdentificador();
        verify(transacao).getTipo();
        verify(transacao).setStatus(eq(StatusTransacao.FALHA));
        verify(transacaoRepository).findByIdentificadorAndStatus(eq("Identificacao"), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:estorno An error occurred"));
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(true);
        doNothing().when(logUseCase).error(Mockito.<String>any());

        // Act and Assert
        assertThrows(TransacaoException.class, () -> transacaoUseCaseImpl.adicionarTransacao(new Transacao()));
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(isNull(), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicionarTransacaoJá existe ume transação pendente com esse identificador."));
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao2() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(true);
        doThrow(new TransacaoException("An error occurred")).when(logUseCase).error(Mockito.<String>any());

        // Act and Assert
        assertThrows(TransacaoException.class, () -> transacaoUseCaseImpl.adicionarTransacao(new Transacao()));
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(isNull(), eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicionarTransacaoJá existe ume transação pendente com esse identificador."));
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao3() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(false);
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        Transacao transacao = new Transacao();

        // Act
        Transacao actualAdicionarTransacaoResult = transacaoUseCaseImpl.adicionarTransacao(transacao);

        // Assert
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(isNull(), eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
        assertEquals("Transação pendente de efetivação", transacao.getDescricaoStatus());
        assertEquals(StatusTransacao.PENDENTE, transacao.getStatus());
        assertSame(transacao, actualAdicionarTransacaoResult);
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao4() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(true);
        doNothing().when(logUseCase).error(Mockito.<String>any());
        Transacao transacao = mock(Transacao.class);
        when(transacao.getIdentificador()).thenReturn("Identificador");

        // Act and Assert
        assertThrows(TransacaoException.class, () -> transacaoUseCaseImpl.adicionarTransacao(transacao));
        verify(transacao).getIdentificador();
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(eq("Identificador"),
                eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicionarTransacaoJá existe ume transação pendente com esse identificador."));
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao5() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(false);
        doNothing().when(transacaoRepository).save(Mockito.<Transacao>any());
        Transacao transacao = mock(Transacao.class);
        doNothing().when(transacao).pendente();
        doNothing().when(transacao).setDescricaoStatus(Mockito.<String>any());
        when(transacao.getIdentificador()).thenReturn("Identificador");

        // Act
        Transacao actualAdicionarTransacaoResult = transacaoUseCaseImpl.adicionarTransacao(transacao);

        // Assert
        verify(transacao).getIdentificador();
        verify(transacao).pendente();
        verify(transacao).setDescricaoStatus(eq("Transação pendente de efetivação"));
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(eq("Identificador"),
                eq(StatusTransacao.PENDENTE));
        verify(transacaoRepository).save(isA(Transacao.class));
        assertSame(transacao, actualAdicionarTransacaoResult);
    }

    /**
     * Method under test: {@link TransacaoUseCaseImpl#adicionarTransacao(Transacao)}
     */
    @Test
    void testAdicionarTransacao6() {
        // Arrange
        when(transacaoRepository.existByIdentificadorAndStatusTransacao(Mockito.<String>any(),
                Mockito.<StatusTransacao>any())).thenReturn(false);
        doNothing().when(logUseCase).error(Mockito.<String>any());
        Transacao transacao = mock(Transacao.class);
        doThrow(new TransacaoException("An error occurred")).when(transacao).pendente();
        when(transacao.getIdentificador()).thenReturn("Identificador");

        // Act and Assert
        assertThrows(TransacaoException.class, () -> transacaoUseCaseImpl.adicionarTransacao(transacao));
        verify(transacao).getIdentificador();
        verify(transacao).pendente();
        verify(transacaoRepository).existByIdentificadorAndStatusTransacao(eq("Identificador"),
                eq(StatusTransacao.PENDENTE));
        verify(logUseCase).error(eq("Erro:adicionarTransacaoAn error occurred"));
    }

    /**
     * Method under test:
     * {@link TransacaoUseCaseImpl#TransacaoUseCaseImpl(TransacaoRepository, LogUseCase, ContaRepository)}
     */
    @Test
    void testNewTransacaoUseCaseImpl() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: Missing observers.
        //   Diffblue Cover was unable to create an assertion.
        //   Add getters for the following fields or make them package-private:
        //     TransacaoUseCaseImpl.contaRepository
        //     TransacaoUseCaseImpl.log
        //     TransacaoUseCaseImpl.transacaoRepository

        // Arrange
        H2TransacaoRepositoryImpl transacaoRepository = new H2TransacaoRepositoryImpl();
        LogUseCaseImpl log = new LogUseCaseImpl();

        // Act
        new TransacaoUseCaseImpl(transacaoRepository, log, new H2ContaRepositoryImpl());

    }
}
