package br.com.carteiradigital.infrastructure.repository.impl;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.infrastructure.entity.TransacaoEntity;
import br.com.carteiradigital.infrastructure.repository.SpringDataTransacaoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class H2TransacaoRepositoryImpl implements TransacaoRepository {

    @Autowired
    private SpringDataTransacaoRepository repository;

    @Autowired
    private ModelMapper mapper;

    @Override
    public void save(Transacao transacao) {
        TransacaoEntity entity = repository.save(mapper.map(transacao, TransacaoEntity.class));
        repository.save(entity);
    }

    @Override
    public List<Transacao> findByIdConta(UUID idConta) {
        return Arrays.asList( mapper.map(repository.findByIdConta(idConta), Transacao[].class));
    }

    @Override
    public Optional<Transacao> findByIdentificadorAndStatus(String identificador, StatusTransacao statusTransacao) {
        Optional<TransacaoEntity> transacaoEntity = repository.findByIdentificadorAndStatus(identificador, statusTransacao);
        return transacaoEntity.map(entity -> mapper.map(entity, Transacao.class));
    }

    @Override
    public Boolean existByIdentificadorAndStatusTransacao(String identificador, StatusTransacao statusTransacao) {
        return repository.existsByIdentificadorAndStatus(identificador, statusTransacao);
    }


}
