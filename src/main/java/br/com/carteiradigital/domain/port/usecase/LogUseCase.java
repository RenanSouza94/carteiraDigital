package br.com.carteiradigital.domain.port.usecase;

public interface LogUseCase {
    void info(String message);
    void warn(String message);
    void error(String message);
    void debug(String message);
}
