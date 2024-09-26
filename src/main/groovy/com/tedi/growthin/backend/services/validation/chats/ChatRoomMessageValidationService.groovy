package com.tedi.growthin.backend.services.validation.chats

import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomMessageException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class ChatRoomMessageValidationService implements ValidationService{

    @Override
    void validate(def chatRoomMessageDto) throws ValidationException {

        if(chatRoomMessageDto == null)
            throw new ChatRoomMessageException("Chat room message not provided")


        chatRoomMessageDto = chatRoomMessageDto as ChatRoomMessageDto

        if(chatRoomMessageDto.senderId == null)
            throw new ChatRoomMessageException("Chat room message sender id can't be empty")

        if(chatRoomMessageDto.receiverId == null)
            throw new ChatRoomMessageException("Chat room message receiver id can't be empty")

        if((chatRoomMessageDto.message == null || chatRoomMessageDto.message.isEmpty()) && chatRoomMessageDto.mediaDto == null){
            throw new ChatRoomMessageException("Chat room message can't be empty. Either provide message or media")
        }
    }

}
