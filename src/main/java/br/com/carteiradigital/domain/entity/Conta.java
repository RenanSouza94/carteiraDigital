package br.com.carteiradigital.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public class Conta {

    public Conta() {
    }

    public Conta(UUID id, String agencia, String numConta, BigDecimal saldo) {
        this.id = id;
        this.agencia = agencia;
        this.numConta = numConta;
        this.saldo = saldo;
    }

    private UUID id;
    private String agencia;
    private String numConta;
    private BigDecimal saldo;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumConta() {
        return numConta;
    }

    public void setNumConta(String numConta) {
        this.numConta = numConta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
}
