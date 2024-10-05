package br.com.carteiradigital.infrastructure.repository;

import br.com.carteiradigital.infrastructure.entity.TransacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataTransacaoRepository extends JpaRepository<TransacaoEntity, UUID> {

    Boolean existByIdentificador(String identificador);

    List<TransacaoEntity> findByIdConta(UUID idConta);
}
