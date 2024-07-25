package com.tedi.growthin.backend.utils.exception.validation.users

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
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
