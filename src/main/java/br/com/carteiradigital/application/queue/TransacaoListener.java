package br.com.carteiradigital.application.queue;

import br.com.carteiradigital.domain.entity.EfetivaTransacao;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TransacaoListener {

    @Autowired
    private TransacaoUseCase transacaoUseCase;

    @Autowired
    private LogUseCase logUseCase;

    @RabbitListener(queues = "transacaoQueue")
    public void receberTransacao(Message message) throws JsonProcessingException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            EfetivaTransacao efetivaTransacao = objectMapper.readValue(message.getBody(), EfetivaTransacao.class);
            transacaoUseCase.efetivarTransacao(efetivaTransacao);
        } catch(Exception e){
            logUseCase.error("ERro message: "+message);
        }

    }



}
