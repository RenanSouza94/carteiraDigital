package br.com.carteiradigital.application.rest.controller;

import br.com.carteiradigital.domain.entity.Conta;
import br.com.carteiradigital.domain.exception.ContaException;
import br.com.carteiradigital.domain.port.usecase.ContaUseCase;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(name = "Api de operações da conta")
@RestController
@Validated
@RequestMapping("/api/conta")
public class ContaController {

    private final LogUseCase log;
    private final ContaUseCase contaUseCase;

    public ContaController(LogUseCase log, ContaUseCase contaUseCase) {
        this.log = log;
        this.contaUseCase = contaUseCase;
    }


    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Saldo retornado com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")})
    @Operation(summary = "Retorno de saldo da conta")
    @GetMapping("/saldo/{idConta}")
    public ResponseEntity<?> calcularSaldo(@PathVariable("idConta") UUID idConta ) {
        try {
            return ResponseEntity.ok(contaUseCase.consultaSaldo(idConta));
        } catch(ContaException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            log.error("Erro:calcularSaldo "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao calcular Saldo");
        }
    }

    @PostConstruct
    public void criarConta(){
        contaUseCase.criarConta(new Conta(UUID.fromString("0133f454-bfbe-4ff3-80ac-5d989a9132a0"), "0699", "5877", new BigDecimal(0)));
        contaUseCase.criarConta(new Conta(UUID.fromString("c45bf3d6-0c25-433b-9894-c74df1907d4a"), "6442", "9999", new BigDecimal(100)));
    }
}
