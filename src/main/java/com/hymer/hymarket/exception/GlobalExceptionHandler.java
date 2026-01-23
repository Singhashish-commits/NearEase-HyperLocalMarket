package com.hymer.hymarket.exception;

import com.hymer.hymarket.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle all the Runtime time Exception
   @ExceptionHandler(RuntimeException.class)
   public ResponseEntity<ErrorResponse> handleRuntimeException(Exception ex, WebRequest request) {
       ErrorResponse errorResponse  = new ErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               "Bad Request",
               ex.getMessage(),
               request.getDescription(false).replace("uri=", "")
       );
       return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
   }


   // Method mismatch exceptio Handler like mismatch from enum for booking status
   @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
       String message = " invalid value for parameter"+ ex.getMessage()+" Expected Type : " + ex.getRequiredType().getSimpleName();

       ErrorResponse errorResponse = new ErrorResponse(
               HttpStatus.BAD_REQUEST.value(),
               "Invalid Parameter",
               message,
               request.getDescription(false).replace("uri=", "")
       );
       return new ResponseEntity<>(errorResponse,HttpStatus.BAD_REQUEST);

   }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> HandleAllException(Exception ex, WebRequest request){
       ErrorResponse errorResponse = new ErrorResponse(
               HttpStatus.INTERNAL_SERVER_ERROR.value(),
               "Internal server error",
               "Something Went Wrong , Please Try Again Later",
               request.getDescription(false).replace("uri=", "")
       );
       ex.printStackTrace();
       return new ResponseEntity<>(errorResponse,HttpStatus.INTERNAL_SERVER_ERROR);
   }



}
