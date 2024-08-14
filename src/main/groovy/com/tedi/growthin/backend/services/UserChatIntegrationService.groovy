package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
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
        String userIdentifier = "[userId = '${currentLoggedInUserId}', username = ${JwtService.extractUsername(userJwtToken)}]"

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

        //chat room not exists -> create it
        def newChatRoomDto = chatRoomService.createChatRoom(chatRoomDto)

        log.info("${userIdentifier} Successfully created chat room: ${newChatRoomDto}")

        return newChatRoomDto
    }

    ChatRoomMessageDto createNewMessage(ChatRoomMessageDto chatRoomMessageDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        String userIdentifier = "[userId = '${currentLoggedInUserId}', username = ${JwtService.extractUsername(userJwtToken)}]"


        chatRoomMessageDto.senderId = currentLoggedInUserId
        chatRoomMessageDto.id = null //remove id if provided as it's a new message

        validationServiceMap["chatRoomMessageValidationService"].validate(chatRoomMessageDto)


        //also validate media if provided
        if (chatRoomMessageDto.mediaDto) {
            chatRoomMessageDto.mediaDto.userId = currentLoggedInUserId
            validationServiceMap["mediaValidationService"].validate(chatRoomMessageDto.mediaDto)
        }

        //if chatRoom id not provided
        if(chatRoomMessageDto.chatRoomId == null) {
            //check if chat with these 2 users exists
            ChatRoomDto chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(
                    (Long) chatRoomMessageDto.senderId,
                    (Long) chatRoomMessageDto.receiverId
            )

            if(chatRoomDto == null){
                log.info("${userIdentifier} Chat room not exists to send message to user with id '${chatRoomMessageDto.receiverId}'. Creating...")
                //if chat not exists -> create a new one
                //and then send message
                chatRoomDto = new ChatRoomDto()
                chatRoomDto.relatedUser2 = new UserDto()
                chatRoomDto.relatedUser2.id = chatRoomMessageDto.receiverId
                chatRoomDto = this.createNewChatRoom(chatRoomDto, authentication)
            }
            chatRoomMessageDto.chatRoomId = chatRoomDto.id
        }

        ChatRoomMessageDto newChatRoomMessageDto = chatRoomService.createChatRoomMessage(chatRoomMessageDto)

        log.info("${userIdentifier} Chat room message was successfully sent to user with id '${chatRoomMessageDto.receiverId}'")
        return newChatRoomMessageDto
    }

    //set messageList isRead

    //listAllChatRoomMessages (with paging)

    //listAllUnreadMessages (with paging)

}
