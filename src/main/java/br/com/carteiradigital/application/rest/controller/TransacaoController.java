package br.com.carteiradigital.application.rest.controller;


import br.com.carteiradigital.application.rest.input.TransacaoRequest;
import br.com.carteiradigital.domain.entity.EfetivaTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Api de transações da carteira digital")
@RestController
@RequestMapping(path = "/api/transacao")
public class TransacaoController {
    private final TransacaoUseCase transacaoUseCase;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    private final RabbitTemplate rabbitTemplate;
    private final LogUseCase log;

    @Value("${nomeFilaTransacao}")
    private String queue;

    @Autowired
    public TransacaoController(TransacaoUseCase transacaoUseCase, RabbitTemplate rabbitTemplate, ModelMapper mapper, LogUseCase log, ObjectMapper objectMapper) {
        this.transacaoUseCase = transacaoUseCase;
        this.rabbitTemplate = rabbitTemplate;
        this.mapper = mapper;
        this.log=log;
        this.objectMapper = objectMapper;
    }

    @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Transação realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Falha na validação"),
        @ApiResponse(responseCode = "500", description = "Erro interno")})
    @Operation(summary = "Cadastra uma nova transação", method = "POST")
    @PostMapping("/adicionarTransacao")
    public ResponseEntity<?> adicionarTransacao(@RequestBody @Valid TransacaoRequest transacao) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(transacaoUseCase.adicionarTransacao(mapper.map(transacao, Transacao.class)));
        } catch(TransacaoException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(Exception e){
            log.error("Erro:adicionarTransacao "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao adicionarTransacao Transacao");
        }

    }

    @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Efetivação enviada com sucesso"),
            @ApiResponse(responseCode = "500", description = "Erro interno")})
    @Operation(summary = "Efetiva uma transação Pendente", method = "PUT")
    @PutMapping("/efetivarTransacao/{identificacao}")
    public ResponseEntity<String> efetivarTransacao(@PathVariable("identificacao") String identificacao, @PathParam("cancelar") boolean cancelar) {
        try {
            byte[] messageBody = objectMapper.writeValueAsBytes(new EfetivaTransacao(identificacao, cancelar));
            MessageProperties messageProperties = new MessageProperties();
            messageProperties.setContentType("application/json");
            rabbitTemplate.convertAndSend(queue, new Message(messageBody, messageProperties));
            return ResponseEntity.ok("Transação enviada com sucesso");
        } catch(Exception e){
            log.error("Erro:efetivarTransacao "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao efetivar transação");
        }
    }

    @GetMapping("/listar/{uuidConta}")
    public ResponseEntity<?> listarTransacoes(@PathVariable("uuidConta") UUID uuidConta ) {
        try {
            return ResponseEntity.ok(transacaoUseCase.listarTransacoes(uuidConta));
        } catch(Exception e){
            log.error("Erro:listarTransacoes "+e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro ao listarTransacoes transação");
        }

    }


}
