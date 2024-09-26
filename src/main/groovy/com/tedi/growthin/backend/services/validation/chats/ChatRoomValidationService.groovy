package com.tedi.growthin.backend.services.validation.chats

import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class ChatRoomValidationService implements ValidationService {

    @Override
    void validate(def chatRoomDto) throws ValidationException {
        if(chatRoomDto == null)
            throw new ChatRoomException("Chat room can't be empty")

        chatRoomDto = chatRoomDto as ChatRoomDto

        if(chatRoomDto.relatedUser1 == null)
            throw new ChatRoomException("RelatedUser1 can't be empty")
        else if(chatRoomDto.relatedUser1.id == null)
            throw new ChatRoomException("RelatedUser1 id can't be empty")


        if(chatRoomDto.relatedUser2 == null)
            throw new ChatRoomException("RelatedUser2 can't be empty")
        else if(chatRoomDto.relatedUser2.id == null)
            throw new ChatRoomException("RelatedUser2 id can't be empty")



    }
}
