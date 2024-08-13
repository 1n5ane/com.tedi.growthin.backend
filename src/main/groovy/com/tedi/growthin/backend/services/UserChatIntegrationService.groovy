package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.chats.ChatRoomService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserChatIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    ChatRoomService chatRoomService

    ChatRoomDto createNewChatRoom(ChatRoomDto chatRoomDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        chatRoomDto.relatedUser1 = new UserDto()
        chatRoomDto.relatedUser1.id = currentLoggedInUserId

        //remove id if provided
        chatRoomDto.id = null

        validationServiceMap["chatRoomValidationService"].validate(chatRoomDto)

        //check if chat with these 2 users already exist
        Boolean chatRoomExists = chatRoomService.checkChatRoomExistsByRelatedUsers(
                (Long) chatRoomDto.relatedUser1.id,
                (Long) chatRoomDto.relatedUser2.id
        )

        if (chatRoomExists) {
            throw new ChatRoomException("Chat room already exists for users")
        }

        def newChatRoomDto = chatRoomService.createChatRoom(chatRoomDto)

        log.info("Successfully created chat room: ${newChatRoomDto}")

        //chat room not exists -> create it
        return newChatRoomDto
    }

}
