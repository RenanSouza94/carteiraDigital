package br.com.carteiradigital.domain.exception;

public class TransacaoException extends RuntimeException{
    public TransacaoException(String message){
        super(message);
    }
}
