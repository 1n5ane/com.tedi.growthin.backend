package com.tedi.growthin.backend.utils.exception

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
