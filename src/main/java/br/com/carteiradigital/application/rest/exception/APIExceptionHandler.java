package br.com.carteiradigital.application.rest.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@RestControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String mensagem = "Há dados incorretos na requisição";

        if (ex instanceof MethodArgumentNotValidException) {
            StringBuilder messageBuilder = new StringBuilder();
            BindingResult result = ((MethodArgumentNotValidException) ex).getBindingResult();
            List<FieldError> errors = result.getFieldErrors();
            for(FieldError error : errors){
                String msg = error.getDefaultMessage().replace("$field", error.getField());
                messageBuilder.append("Erro no campo: ").append(error.getField()).append(" - mensagem de erro: ").append(msg).append(" - ");
            }
            mensagem = messageBuilder.toString();

        } else if (ex instanceof ConstraintViolationException) {
            StringBuilder messageBuilder = new StringBuilder();
            AtomicInteger index = new AtomicInteger(0);
            Set<ConstraintViolation<?>> constraintViolations = ((ConstraintViolationException) ex).getConstraintViolations();

            constraintViolations.forEach(violation -> {
                String field = violation.getPropertyPath().toString();
                messageBuilder.append("Erro no campo: ").append(field).append(" - mensagem de erro: ").append(violation.getMessage()).append(" - ");;
                if (index.getAndIncrement() < constraintViolations.size() - 1) {
                    messageBuilder.append(" | ");
                }
            });

            mensagem = messageBuilder.toString();
        }

        return super.handleExceptionInternal(ex, mensagem, headers, statusCode, request);
    }


}
