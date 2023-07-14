package com.jhc.orderservice.exception;

import com.jhc.orderservice.external.client.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception)
    {
        return new ResponseEntity<>(new ErrorResponse()
                .builder().errorMessage(exception.getMessage()).
                errorCode(exception.getErrorCode()).build(),
                HttpStatus.valueOf(exception.getStatus()) );
    }
}
