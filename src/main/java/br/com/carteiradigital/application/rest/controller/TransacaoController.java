package br.com.carteiradigital.application.rest.controller;


import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.port.usecase.ContaUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Tag(name = "Api de transações da carteira digital")
@RestController
@RequestMapping("/api/transacao")
public class TransacaoController {
    private final TransacaoUseCase transacaoUseCase;
    private final ContaUseCase contaUseCase;

    private final JmsTemplate jmsTemplate;

    @Value("${nomeFilaTransacao}")
    private String queue;

    @Autowired
    public TransacaoController(TransacaoUseCase transacaoUseCase, ContaUseCase contaUseCase, JmsTemplate jmsTemplate) {
        this.transacaoUseCase = transacaoUseCase;
        this.contaUseCase = contaUseCase;
        this.jmsTemplate = jmsTemplate;
    }

    @PostMapping("/adicionarTransacao")
    public ResponseEntity<Transacao> adicionarTransacao(@RequestBody Transacao transacao) {
        transacaoUseCase.adicionarTransacao(transacao);
        return ResponseEntity.ok(transacaoUseCase.adicionarTransacao(transacao));
    }

    @PostMapping("/efetivarTransacao")
    public ResponseEntity<String> efetivarTransacao(@RequestBody Transacao transacao) {
        jmsTemplate.convertAndSend(queue, transacao);
        return ResponseEntity.ok("Transação enviada com sucesso");
    }

    @GetMapping("/transacoes/{uuidConta}")
    public ResponseEntity<List<Transacao>> listarTransacoes(@PathVariable("idConta") UUID idConta ) {
        return ResponseEntity.ok(transacaoUseCase.listarTransacoes(idConta));
    }

    @GetMapping("/saldo")
    public ResponseEntity<BigDecimal> calcularSaldo(@PathVariable("idConta") UUID idConta ) {
        return ResponseEntity.ok(contaUseCase.consultaSaldo(idConta));
    }
}
