package br.com.carteiradigital.infrastructure.entity;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.TipoTransacao;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transacao")
@Data
public class TransacaoEntity {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    private TipoTransacao tipo;
    private BigDecimal valor;
    private LocalDateTime dataHoraCriacao;
    private LocalDateTime dataHoraEfetivacao;
    private String identificador;
    private StatusTransacao status;
    private String descricao;
    private String descricaoStatus;
    private UUID idConta;

}
