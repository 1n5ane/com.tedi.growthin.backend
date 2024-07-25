package com.tedi.growthin.backend.utils.exception.validation.connections

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class UserConnectionRequestException extends ValidationException {
    UserConnectionRequestException() {
        super()
    }

    UserConnectionRequestException(String message) {
        super(message)
    }
}
