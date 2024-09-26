package com.tedi.growthin.backend.utils.exception.validation.notifications

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class NotificationException extends ValidationException {

    NotificationException() {}

    NotificationException(String message) {
        super(message)
    }
}
