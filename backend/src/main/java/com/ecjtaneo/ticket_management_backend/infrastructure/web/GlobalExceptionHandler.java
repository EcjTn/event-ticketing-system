package com.ecjtaneo.ticket_management_backend.infrastructure.web;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    //automatically catches ErrorResponse exceptions and converts to clean JSON response. (RFC 9457)
}
