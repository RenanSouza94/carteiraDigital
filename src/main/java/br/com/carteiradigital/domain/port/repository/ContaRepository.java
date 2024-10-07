package br.com.carteiradigital.domain.port.repository;

import java.math.BigDecimal;
import java.util.UUID;

public interface ContaRepository {
    void atualizaSaldo(BigDecimal valor, UUID idConta);

    BigDecimal consultaSaldo(UUID idConta);
}
