package br.com.carteiradigital.application.rest.input;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class NovaTransacaoRequest {

    @NotBlank(message = "O atributo tipo é obrigatório")
    private TipoTransacao tipo;
    @NotBlank(message = "O atributo valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor inválido")
    private BigDecimal valor;
    @NotBlank(message = "O atributo identificador é obrigatório")
    private String identificador;
    @NotBlank(message = "O atributo status é obrigatório")
    private StatusTransacao status;
    private String descricao;


}
