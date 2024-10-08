package br.com.carteiradigital.domain.adapters;

import br.com.carteiradigital.domain.entity.Conta;
import br.com.carteiradigital.domain.exception.ContaException;
import br.com.carteiradigital.domain.port.repository.ContaRepository;
import br.com.carteiradigital.domain.port.usecase.ContaUseCase;
import br.com.carteiradigital.domain.port.usecase.LogUseCase;

import java.math.BigDecimal;
import java.util.UUID;

public class ContaUseCaseImpl implements ContaUseCase {

    private final ContaRepository contaRepository;
    private final LogUseCase log;

    public ContaUseCaseImpl(ContaRepository contaRepository, LogUseCase log) {
        this.contaRepository = contaRepository;
        this.log = log;
    }

    @Override
    public void atualizaSaldo(BigDecimal valor, UUID idConta) {
        contaRepository.atualizaSaldo(valor, idConta);
    }

    @Override
    public BigDecimal consultaSaldo(UUID idConta) {
        BigDecimal valor = contaRepository.consultaSaldo(idConta);
        if(valor == null){
            throw new ContaException("Conta inexistente");
        }
        return contaRepository.consultaSaldo(idConta);
    }

    @Override
    public void criarConta(Conta conta) {
        contaRepository.save(conta);
    }
}
