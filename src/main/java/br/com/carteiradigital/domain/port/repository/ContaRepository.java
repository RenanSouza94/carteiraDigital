package br.com.carteiradigital.domain.port.repository;

import br.com.carteiradigital.domain.entity.Conta;

import java.math.BigDecimal;
import java.util.UUID;

public interface ContaRepository {
    void atualizaSaldo(BigDecimal valor, UUID idConta);

    BigDecimal consultaSaldo(UUID idConta);

    void save(Conta conta);
}
