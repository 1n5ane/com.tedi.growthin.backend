package com.tedi.growthin.backend.utils.exception.validation.profiles

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class UserProfileException extends ValidationException {

    UserProfileException() {}

    UserProfileException(String message) {
        super(message)
    }
}
