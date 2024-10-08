package br.com.carteiradigital.application.rest.input;

import br.com.carteiradigital.domain.entity.TipoTransacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class TransacaoRequest {

    @NotNull(message = "O atributo tipo é obrigatório")
    private TipoTransacao tipo;
    @NotNull(message = "O atributo valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor inválido")
    private BigDecimal valor;
    @NotBlank(message = "O atributo identificador é obrigatório")
    @NotNull(message = "O atributo identificador é obrigatório")
    private String identificador;
    private String descricao;
    @NotNull(message = "O atributo idConta é obrigatório")
    private UUID idConta;


}
