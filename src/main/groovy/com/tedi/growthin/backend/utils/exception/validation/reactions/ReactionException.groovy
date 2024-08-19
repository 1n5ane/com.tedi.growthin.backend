package com.tedi.growthin.backend.utils.exception.validation.reactions

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class ReactionException extends ValidationException{
    def ReactionException() {}

    def ReactionException(String message) {
        super(message)
    }
}
