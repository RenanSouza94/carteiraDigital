package br.com.carteiradigital.infrastructure.repository;

import br.com.carteiradigital.infrastructure.entity.ContaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.UUID;

public interface SpringDataContaRepository extends JpaRepository<ContaEntity, UUID> {

    @Query(value = "SELECT c.saldo FROM Conta c WHERE c.id = :id", nativeQuery = true)
    BigDecimal findSaldoById(@Param("id")  UUID id);

    @Query(value = "UPDATE CONTA SET SALDO = (SALDO + :valor) WHERE ID = :idConta ", nativeQuery = true)
    @Modifying
    void atualizaSaldo(@Param("idConta") UUID idConta, @Param("valor") BigDecimal valor);
}
