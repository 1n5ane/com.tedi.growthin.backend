package com.tedi.growthin.backend.utils.exception

import org.springframework.stereotype.Component

@Component
class ForbiddenException extends RuntimeException{
    ForbiddenException() {
        super()
    }

    ForbiddenException(String message) {
        super(message)
    }
}
