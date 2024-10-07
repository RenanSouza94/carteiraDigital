package br.com.carteiradigital.infrastructure.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "conta")
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
