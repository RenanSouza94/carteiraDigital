package br.com.carteiradigital.domain.adapters;

import br.com.carteiradigital.domain.entity.Conta;
import br.com.carteiradigital.domain.exception.ContaException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContaUseCaseImplTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private LogUseCase logUseCase;

    @InjectMocks
    private ContaUseCaseImpl contaUseCaseImpl;

    private UUID idConta;
    private Conta conta;

    @BeforeEach
    void setUp() {
        idConta = UUID.fromString("0133f454-bfbe-4ff3-80ac-5d989a9132a0");
        conta = new Conta();
        conta.setId(idConta);
        conta.setSaldo(BigDecimal.valueOf(100.0));
    }

    @Test
    void deveAtualizarSaldoComSucesso() {

        BigDecimal valor = BigDecimal.valueOf(50.0);
        contaUseCaseImpl.atualizaSaldo(valor, idConta);
        verify(contaRepository, times(1)).atualizaSaldo(valor, idConta);
    }

    @Test
    void consultaSaldo_deveRetornarSaldoQuandoContaExistir() {
        when(contaRepository.findById(idConta)).thenReturn(conta);

        BigDecimal saldo = contaUseCaseImpl.consultaSaldo(idConta);

        assertEquals(BigDecimal.valueOf(100.00), saldo);
        verify(contaRepository).findById(idConta);
    }

    @Test
    void deveCriarContaComSucesso() {

        Conta novaConta = new Conta();
        novaConta.setId(idConta);
        novaConta.setSaldo(BigDecimal.valueOf(200.0));

        contaUseCaseImpl.criarConta(novaConta);

        verify(contaRepository, times(1)).save(novaConta);
    }
}
