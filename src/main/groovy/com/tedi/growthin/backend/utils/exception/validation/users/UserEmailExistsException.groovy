package com.tedi.growthin.backend.utils.exception.validation.users

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class UserEmailExistsException extends ValidationException {

    UserEmailExistsException() {
        super()
    }

    UserEmailExistsException(String message) {
        super(message)
    }
}
