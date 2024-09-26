package com.tedi.growthin.backend.utils.exception.validation.chats

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class ChatRoomException extends ValidationException {

    ChatRoomException() {
    }

    ChatRoomException(String message) {
        super(message)
    }
}
