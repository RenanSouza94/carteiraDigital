package br.com.carteiradigital.domain.adapters;

import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class TransacaoUseCaseImpl implements TransacaoUseCase {

    private TransacaoRepository transacaoRepository;
    private ContaRepository contaRepository;
    private LogUseCase log;


    public TransacaoUseCaseImpl(TransacaoRepository transacaoRepository, LogUseCase log, ContaRepository contaRepository){
        this.transacaoRepository = transacaoRepository;
        this.contaRepository = contaRepository;
        this.log = log;
    }

    @Override
    public void efetivarTransacao(Transacao transacao) throws TransacaoException {

        switch (transacao.getTipo()){
            case ADICAO, COMPRA -> adicao(transacao);
            case RETIRADA -> retirada(transacao);
            case CANCELAMENTO -> cancelamento(transacao);
            case ESTORNO -> estorno(transacao);
        }


    }

    @Override
    public Transacao adicionarTransacao(Transacao transacao) {
        if(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.PENDENTE)){
            throw new TransacaoException("Já existe ume transação pendente com esse identificador.");
        }
        transacao.setStatus(StatusTransacao.PENDENTE);
        transacao.setDescricaoStatus("Transação pendente de efetivação");
        transacao.setDataHoraCriacao(LocalDateTime.now());
        transacaoRepository.save(transacao);
        return transacao;
    }

    private void adicao(Transacao transacao){
        try {
            if(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.CONCLUIDA)){
                transacao.setStatus(StatusTransacao.FALHA);
                transacao.setDescricaoStatus("Transação em duplicidade");
            } else{
                transacao.setStatus(StatusTransacao.CONCLUIDA);
                transacao.setDataHoraEfetivacao(LocalDateTime.now());
            }
            transacaoRepository.save(transacao);
        } catch(Exception e){
            log.error("Erro:adicao: "+e.getMessage());
            throw new TransacaoException("Ocorreu uma falha na adição da transação");
        }
    }

    private void retirada(Transacao transacao){
        try {
            BigDecimal saldoAtual = contaRepository.consultaSaldo(transacao.getIdConta());
            if(saldoAtual.compareTo(transacao.getValor()) >= 0){
                contaRepository.atualizaSaldo(transacao.getValor(), transacao.getIdConta());
                transacao.setStatus(StatusTransacao.CONCLUIDA);
            } else{
                transacao.setStatus(StatusTransacao.FALHA);
                transacao.setDescricaoStatus("Saldo insuficiente");
            }

        } catch(Exception e){
            log.error("Erro:retirada "+e.getMessage());
            transacao.setStatus(StatusTransacao.FALHA);
            transacao.setDescricaoStatus("Ocorreu uma falha ao atualizar transação");
        }
        transacaoRepository.save(transacao);
    }

    private void cancelamento(Transacao transacao){
        try {

        } catch(Exception e){
            log.error("Erro:cancelamento: "+e.getMessage());
            throw new TransacaoException("Ocorreu uma falha no cancelamento da transação");
        }
    }
    private void estorno(Transacao transacao){
        try {

        } catch(Exception e){
            log.error("Erro:estorno: "+e.getMessage());
            throw new TransacaoException("Ocorreu uma falha no estorno da transação");
        }
    }

    @Override
    public List<Transacao> listarTransacoes(UUID idConta) {

        return transacaoRepository.findByIdConta(idConta);
    }

}
