package br.com.carteiradigital.domain.port.usecase;

import br.com.carteiradigital.domain.entity.Conta;

import java.math.BigDecimal;
import java.util.UUID;

public interface ContaUseCase {
    void atualizaSaldo(BigDecimal novoValor, UUID idConta);
    BigDecimal consultaSaldo(UUID idConta);

    void criarConta(Conta conta);
}
