package br.com.carteiradigital.application.jms;

import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TransacaoListener {
    @Autowired
    private JmsTemplate jmsTemplate;

    @Autowired
    private TransacaoUseCase transacaoUseCase;

    @JmsListener(destination = "transacaoQueue")
    public void receberTransacao(Transacao transacao) {
        transacaoUseCase.efetivarTransacao(transacao);
    }



}
