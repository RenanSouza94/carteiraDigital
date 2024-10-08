package br.com.carteiradigital.domain.port.usecase;

import br.com.carteiradigital.domain.entity.EfetivaTransacao;
import br.com.carteiradigital.domain.entity.Transacao;

import java.util.List;
import java.util.UUID;

public interface TransacaoUseCase {

    Transacao adicionarTransacao(Transacao transacao);
    void efetivarTransacao(EfetivaTransacao efetivaTransacao);
    List<Transacao> listarTransacoes(UUID idConta);
}
