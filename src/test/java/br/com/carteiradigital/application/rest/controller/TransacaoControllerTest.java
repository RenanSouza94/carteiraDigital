package br.com.carteiradigital.application.rest.controller;

import br.com.carteiradigital.application.rest.input.TransacaoRequest;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransacaoControllerTest {

    @InjectMocks
    private TransacaoController transacaoController;

    @Mock
    private TransacaoUseCase transacaoUseCase;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private ModelMapper mapper;

    @Mock
    private LogUseCase log;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testAdicionarTransacao_Success() {
        TransacaoRequest request = new TransacaoRequest();
        Transacao transacao = new Transacao();
        when(mapper.map(request, Transacao.class)).thenReturn(transacao);
        when(transacaoUseCase.adicionarTransacao(transacao)).thenReturn(transacao);

        ResponseEntity<?> response = transacaoController.adicionarTransacao(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(transacao, response.getBody());
    }

    @Test
    public void testAdicionarTransacao_Failure() {
        TransacaoRequest request = new TransacaoRequest();
        when(mapper.map(request, Transacao.class)).thenReturn(new Transacao());
        when(transacaoUseCase.adicionarTransacao(any())).thenThrow(new TransacaoException("Erro de validação"));

        ResponseEntity<?> response = transacaoController.adicionarTransacao(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Erro de validação", response.getBody());
    }

    @Test
    public void testEfetivarTransacao_Success() throws Exception {
        String identificacao = "ADD55878";
        boolean cancelar = false;

        ResponseEntity<String> response = transacaoController.efetivarTransacao(identificacao, cancelar);

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(rabbitTemplate).convertAndSend(any(), messageCaptor.capture());

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transação enviada com sucesso", response.getBody());
        assertEquals("application/json", messageCaptor.getValue().getMessageProperties().getContentType());
    }

    @Test
    public void testEfetivarTransacao_Failure() throws Exception {
        String identificacao = "ADD55878";
        boolean cancelar = false;
        doThrow(new RuntimeException("Erro ao enviar mensagem")).when(rabbitTemplate).convertAndSend(Optional.ofNullable(any()), any());

        ResponseEntity<String> response = transacaoController.efetivarTransacao(identificacao, cancelar);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro ao efetivar transação", response.getBody());
    }

    @Test
    public void testListarTransacoes_Success() {
        UUID uuidConta = UUID.randomUUID();
        when(transacaoUseCase.listarTransacoes(uuidConta)).thenReturn(null); // Retorne o que for necessário

        ResponseEntity<?> response = transacaoController.listarTransacoes(uuidConta);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testListarTransacoes_Failure() {
        UUID uuidConta = UUID.randomUUID();
        when(transacaoUseCase.listarTransacoes(uuidConta)).thenThrow(new RuntimeException("Erro ao listar"));

        ResponseEntity<?> response = transacaoController.listarTransacoes(uuidConta);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro ao listarTransacoes transação", response.getBody());
    }
}
