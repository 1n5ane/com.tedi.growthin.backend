package com.tedi.growthin.backend.utils.exception


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
