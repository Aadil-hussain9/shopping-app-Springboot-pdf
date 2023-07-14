package com.jhc.ProductService.exception;

import com.jhc.ProductService.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ExceptionHandler(ProductServiceCustomException.class)
    public ResponseEntity<ErrorResponse> handleProductServiceException(ProductServiceCustomException exception)
    {
        return new ResponseEntity<>(new ErrorResponse()
                .builder().errorMessage(exception.getMessage()).errorCode(exception.getErrorCode()).build(), HttpStatus.NOT_FOUND );
    }
}
