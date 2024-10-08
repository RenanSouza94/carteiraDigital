package br.com.carteiradigital.domain.adapters;

import br.com.carteiradigital.domain.entity.EfetivaTransacao;
import br.com.carteiradigital.domain.entity.StatusTransacao;
import br.com.carteiradigital.domain.entity.Transacao;
import br.com.carteiradigital.domain.exception.TransacaoException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.repository.TransacaoRepository;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import br.com.carteiradigital.domain.port.usecase.TransacaoUseCase;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
    @Transactional
    public void efetivarTransacao(EfetivaTransacao efetivaTransacao) throws TransacaoException {

        Optional<Transacao> transacaoOpt = transacaoRepository.findByIdentificadorAndStatus(efetivaTransacao.getIdentificacao(), StatusTransacao.PENDENTE);

        if(transacaoOpt.isPresent()){
            Transacao transacao = transacaoOpt.get();

            if(efetivaTransacao.isCancelar()){
                transacao.cancelar();
                transacaoRepository.save(transacao);
                return;
            }

            switch (transacao.getTipo()){
                case RETIRADA, COMPRA -> retirada(transacao);
                case ADICAO -> adicao(transacao);
                case ESTORNO -> estorno(transacao);
            }
        }

    }

    @Override
    public Transacao adicionarTransacao(Transacao transacao) {
        try {
            if(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.PENDENTE)){
                throw new TransacaoException("Já existe ume transação pendente com esse identificador.");
            }
            transacao.pendente();
            transacao.setDescricaoStatus("Transação pendente de efetivação");
            transacaoRepository.save(transacao);
            return transacao;
        } catch(Exception e){
            log.error("Erro:adicionarTransacao"+e.getMessage());
            throw e;
        }

    }

    private void adicao(Transacao transacao){
        try {
            if(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.CONCLUIDA)){
                transacao.setStatus(StatusTransacao.FALHA);
                transacao.setDescricaoStatus("Transação em duplicidade");
            } else{
                transacao.concluir();
                contaRepository.atualizaSaldo(transacao.getValor(), transacao.getIdConta());
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
                contaRepository.atualizaSaldo(transacao.getValor().negate(), transacao.getIdConta());
                transacao.concluir();
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

    private void estorno(Transacao transacao){
        try {
            if(transacaoRepository.existByIdentificadorAndStatusTransacao(transacao.getIdentificador(), StatusTransacao.CONCLUIDA)){
                transacao.concluir();
                transacao.setDescricaoStatus("Estorno");
                contaRepository.atualizaSaldo(transacao.getValor(), transacao.getIdConta());
            } else{
                transacao.setStatus(StatusTransacao.FALHA);
                transacao.setDescricaoStatus("Não foi encontrado a transação para estornar");
            }
        } catch(Exception e){
            log.error("Erro:estorno "+e.getMessage());
            transacao.setStatus(StatusTransacao.FALHA);
            transacao.setDescricaoStatus("Ocorreu uma falha ao cancelar transação");
        }

        transacaoRepository.save(transacao);
    }

    @Override
    public List<Transacao> listarTransacoes(UUID idConta) {
        return transacaoRepository.findByIdConta(idConta);
    }


}
