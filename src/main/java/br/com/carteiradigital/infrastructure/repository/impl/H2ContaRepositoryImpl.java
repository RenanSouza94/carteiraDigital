package br.com.carteiradigital.infrastructure.repository.impl;

import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.infrastructure.repository.SpringDataContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.UUID;
@Component
public class H2ContaRepositoryImpl implements ContaRepository {

    @Autowired
    private SpringDataContaRepository repository;

    @Override
    public void atualizaSaldo(BigDecimal valor, UUID idConta) {
        repository.atualizaSaldo(idConta, valor);
    }

    @Override
    public BigDecimal consultaSaldo(UUID idConta) {
        return repository.findSaldoById(idConta);
    }
}
