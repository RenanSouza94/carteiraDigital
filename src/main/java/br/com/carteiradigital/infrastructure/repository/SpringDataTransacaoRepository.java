package br.com.carteiradigital.infrastructure.repository;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.infrastructure.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataTransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {

    Boolean existByIdentificadorAndStatus(String identificador, StatusTransacao status);

    List<TransacaoEntity> findByIdConta(UUID idConta);

    Optional<TransacaoEntity> findByIdentificadorAndStatus(String identificador, StatusTransacao status);
}
