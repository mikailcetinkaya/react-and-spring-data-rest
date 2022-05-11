package com.kuehne.payroll;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RestExceptionHandler {

    Logger logger = LoggerFactory.getLogger(SpringDataJpaUserDetailsService.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handlyMyCustomException(Exception e) {
        logger.error("error occurred {}", e.getCause()!=null?e.getCause().getMessage():e.toString());
        e.printStackTrace();
        return new ResponseEntity<>( e.getCause()!=null?e.getCause().getMessage():e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}