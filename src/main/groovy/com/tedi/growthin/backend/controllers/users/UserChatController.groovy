package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.UserChatIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/chat")
@Slf4j
class UserChatController {

    @Autowired
    JwtService jwtService

    @Autowired
    UserChatIntegrationService userChatIntegrationService


    @PostMapping(value = "/{userId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewChatRoom(@PathVariable("userId") String userId, Authentication authentication) {
        //create new chat with userId
        def response = [
                "success" : true,
                "chatRoom": null,
                "error"   : ""
        ]

        try {
            userId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid userId '${userId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        //relatedUser1 (currentLoggedInUser) starts chat with user with userId
        ChatRoomDto chatRoomDto = new ChatRoomDto()
        chatRoomDto.relatedUser2 = new UserDto()
        chatRoomDto.relatedUser2.id = userId.toLong()

        try {
            def createdUserConnectionRequestDto = userChatIntegrationService.createNewChatRoom(chatRoomDto, authentication)
            response["chatRoom"] = createdUserConnectionRequestDto
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to create chat room with user with id '${userId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} failed to create chat room with user with id '${userId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)


    }
}
