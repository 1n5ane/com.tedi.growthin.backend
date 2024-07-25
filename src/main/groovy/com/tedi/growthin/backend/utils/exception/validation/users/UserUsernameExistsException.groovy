package com.tedi.growthin.backend.utils.exception.validation.users

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class UserUsernameExistsException extends ValidationException {

    UserUsernameExistsException() {
        super()
    }

    UserUsernameExistsException(String message) {
        super(message)
    }
}
