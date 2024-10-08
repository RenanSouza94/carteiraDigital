package br.com.carteiradigital.application.rest.controller;

import br.com.carteiradigital.application.rest.input.TransacaoRequest;
import br.com.carteiradigital.domain.entity.EfetivaTransacao;
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
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Mock
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transacaoController).build();
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
    void deveEfetivarTransacaoComSucesso() throws Exception {
        // Arrange
        String identificacao = UUID.randomUUID().toString();
        boolean cancelar = false;

        byte[] messageBody = new byte[0];
        when(objectMapper.writeValueAsBytes(any())).thenReturn(messageBody);
        doNothing().when(rabbitTemplate).convertAndSend(messageBody);

        // Act & Assert
        mockMvc.perform(put("/api/transacao/efetivarTransacao/{identificacao}", identificacao)
                        .param("cancelar", String.valueOf(cancelar)))
                .andExpect(status().isOk());

    }

    @Test
    void deveRetornarErroQuandoFalharAoEfetivarTransacao() throws Exception {
        // Arrange
        String identificacao = UUID.randomUUID().toString();
        boolean cancelar = false;
        EfetivaTransacao efetivaTransacao = new EfetivaTransacao(identificacao, cancelar);

        when(objectMapper.writeValueAsBytes(efetivaTransacao)).thenThrow(new RuntimeException("Erro ao criar mensagem"));

        // Act & Assert
        mockMvc.perform(put("/api/transacao/efetivarTransacao/{identificacao}", identificacao)
                        .param("cancelar", String.valueOf(cancelar)))
                .andExpect(status().isInternalServerError());

        verify(rabbitTemplate, times(0)).convertAndSend(anyString(), any(Message.class));
    }

    @Test
    void deveEnviarTransacaoComSucessoParaFila() throws Exception {
        // Arrange
        String identificacao = UUID.randomUUID().toString();
        boolean cancelar = false;

        byte[] messageBody = new byte[0];

        when(objectMapper.writeValueAsBytes(any())).thenReturn(messageBody);
        doNothing().when(rabbitTemplate).convertAndSend(anyString(), eq(messageBody));

        // Act
        ResponseEntity<String> response = transacaoController.efetivarTransacao(identificacao, cancelar);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Transação enviada com sucesso", response.getBody());
    }

    @Test
    void deveRetornarErroAoTentarEfetivarTransacaoComErroDeConversao() throws Exception {
        // Arrange
        String identificacao = UUID.randomUUID().toString();
        boolean cancelar = false;
        EfetivaTransacao efetivaTransacao = new EfetivaTransacao(identificacao, cancelar);

        when(objectMapper.writeValueAsBytes(efetivaTransacao)).thenThrow(new RuntimeException("Erro ao converter mensagem"));

        // Act
        ResponseEntity<String> response = transacaoController.efetivarTransacao(identificacao, cancelar);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro ao efetivar transação", response.getBody());
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
