package br.com.carteiradigital.infrastructure.service;

import br.com.carteiradigital.domain.port.usecase.LogUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogUseCaseImpl implements LogUseCase {
    @Override
    public void info(String message) {
        log.info(message);
    }

    @Override
    public void warn(String message) {
        log.warn(message);
    }

    @Override
    public void error(String message) {
        log.error(message);
    }

    @Override
    public void debug(String message) {
        log.debug(message);
    }
}
