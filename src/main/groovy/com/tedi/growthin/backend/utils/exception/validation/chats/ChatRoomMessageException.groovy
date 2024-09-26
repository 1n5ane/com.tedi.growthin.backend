package com.tedi.growthin.backend.utils.exception.validation.chats

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class ChatRoomMessageException extends ValidationException {

    ChatRoomMessageException() {}

    ChatRoomMessageException(String message) {
        super(message)
    }
}
