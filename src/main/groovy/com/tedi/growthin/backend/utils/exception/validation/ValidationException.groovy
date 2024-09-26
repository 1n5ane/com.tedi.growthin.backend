package com.tedi.growthin.backend.utils.exception.validation

import org.springframework.stereotype.Component

@Component
abstract class ValidationException extends RuntimeException{
    ValidationException() {
        super()
    }

    ValidationException(String message){
        super(message)
    }
}