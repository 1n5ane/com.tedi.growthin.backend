package com.tedi.growthin.backend.services.validation.chats

import com.tedi.growthin.backend.dtos.chats.ChatRoomMessagesIsReadDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomMessageException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class ChatRoomMessageIsReadValidationService implements ValidationService{

    @Override
    void validate(def chatRoomMessageIsReadDto) throws ValidationException {

        if(chatRoomMessageIsReadDto == null){
            throw new ChatRoomMessageException("Chat room message isRead body can't be empty")
        }

        chatRoomMessageIsReadDto = chatRoomMessageIsReadDto as ChatRoomMessagesIsReadDto

        if(chatRoomMessageIsReadDto.senderId == null){
            throw new ChatRoomMessageException("Sender id can't be empty")
        }

        if(chatRoomMessageIsReadDto.receiverId == null){
            throw new ChatRoomMessageException("Receiver id can't be empty")
        }

        if(chatRoomMessageIsReadDto.messageIds == null || chatRoomMessageIsReadDto.messageIds.isEmpty()){
            throw new ChatRoomMessageException("No message ids provided")
        }

        if(chatRoomMessageIsReadDto.isRead == null){
            throw new ChatRoomMessageException("Boolean isRead can't be set to null")
        }

    }
}
