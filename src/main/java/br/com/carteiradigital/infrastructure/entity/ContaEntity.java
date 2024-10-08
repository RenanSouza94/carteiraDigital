package br.com.carteiradigital.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "CONTA")
@Data
public class ContaEntity {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    private String agencia;
    private String numConta;
    private BigDecimal saldo;


}
