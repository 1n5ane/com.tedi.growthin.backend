package com.tedi.growthin.backend.utils.exception

import org.springframework.stereotype.Component

@Component
class UserValidationException extends ValidationException{

    UserValidationException() {
        super()
    }

    UserValidationException(String message) {
        super(message)
    }
}
