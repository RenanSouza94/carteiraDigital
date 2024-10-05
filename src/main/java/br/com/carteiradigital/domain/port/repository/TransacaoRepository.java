package br.com.carteiradigital.domain.port.repository;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.Transacao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransacaoRepository {

    void save(Transacao transacao);
    List<Transacao> findByIdConta(UUID idConta);
    Optional<Transacao> findByIdentificador(String identificador);
    Boolean existByIdentificadorAndStatusTransacao(String identificador, StatusTransacao statusTransacao);


}
