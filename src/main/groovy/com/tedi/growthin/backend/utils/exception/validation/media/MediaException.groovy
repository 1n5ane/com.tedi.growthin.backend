package com.tedi.growthin.backend.utils.exception.validation.media

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class MediaException extends ValidationException{
    MediaException() {}

    MediaException(String message) {
        super(message)
    }
}
