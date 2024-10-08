package br.com.carteiradigital.infrastructure.repository.impl;

import br.com.carteiradigital.domain.entity.Conta;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.infrastructure.entity.ContaEntity;
import br.com.carteiradigital.infrastructure.repository.SpringDataContaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
@Component
public class H2ContaRepositoryImpl implements ContaRepository {

    @Autowired
    private SpringDataContaRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public void atualizaSaldo(BigDecimal valor, UUID idConta) {
        repository.atualizaSaldo(idConta, valor);
    }

    @Override
    public BigDecimal consultaSaldo(UUID idConta) {
        return repository.findSaldoById(idConta);
    }

    @Override
    public void save(Conta conta) {
        repository.save(mapper.map(conta, ContaEntity.class));
    }

    @Override
    public Conta findById(UUID id) {
        Optional<ContaEntity> entity = repository.findById(id);
        if(entity.isPresent()){
            return mapper.map(entity, Conta.class);
        }
        return null;
    }
}
