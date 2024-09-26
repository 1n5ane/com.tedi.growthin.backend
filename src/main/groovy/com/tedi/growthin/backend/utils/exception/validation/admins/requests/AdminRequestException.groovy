package com.tedi.growthin.backend.utils.exception.validation.admins.requests

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component

class AdminRequestException extends ValidationException {

    AdminRequestException() {}

    AdminRequestException(String message) {
        super(message)
    }
}
