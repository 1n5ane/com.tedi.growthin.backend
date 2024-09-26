package com.tedi.growthin.backend.utils.exception.validation.connections

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class UserConnectionException extends ValidationException {
    UserConnectionException() {}

    UserConnectionException(String message) {
        super(message)
    }

}
