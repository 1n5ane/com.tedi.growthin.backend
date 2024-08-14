package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessagesIsReadDto
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
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
            def createdChatRoomDto = userChatIntegrationService.createNewChatRoom(chatRoomDto, authentication)
            response["chatRoom"] = createdChatRoomDto
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

    @PostMapping(value = "/{userId}/messages", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewMessage(@PathVariable("userId") String userId,
                         @RequestBody ChatRoomMessageDto chatRoomMessageDto,
                         Authentication authentication) {

        //create new chat with userId
        def response = [
                "success"        : true,
                "chatRoomMessage": null,
                "error"          : ""
        ]

        try {
            userId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid userId '${userId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        chatRoomMessageDto.receiverId = userId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def createdChatRoomMessageDto = userChatIntegrationService.createNewMessage(chatRoomMessageDto, authentication)
            response["chatRoomMessage"] = createdChatRoomMessageDto
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to send message to user with id '${userId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} failed to send message to user with id '${userId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = "/{userId}/messages/read", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def setMessagesIsRead(@PathVariable("userId") String userId,
                          @RequestBody ChatRoomMessagesIsReadDto chatRoomMessagesIsReadDto,
                          Authentication authentication) {

        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            userId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid userId '${userId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        //messages sent by userId
        chatRoomMessagesIsReadDto.senderId = userId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def success = userChatIntegrationService.setIsReadToMessages(chatRoomMessagesIsReadDto, authentication)
            response["success"] = success
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to set isRead=${chatRoomMessagesIsReadDto.isRead} to messages received by user with id '${userId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to set isRead=${chatRoomMessagesIsReadDto.isRead} to messages received by user with id '${userId}':  ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    //listAllChatRoom for currentLoggesInUser
    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllChatRooms(@RequestParam(name = "page", defaultValue = "0") Integer page,
                         @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                         @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                         @RequestParam(name = "order", defaultValue = "desc") String order,
                         Authentication authentication) {

        def response = [
                "success"    : true,
                "hasNextPage": false,
                "chatRooms"  : [],
                "totalPages" : null,
                "error"      : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userChatRoomListDto = userChatIntegrationService.findAllUserChatRooms(page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userChatRoomListDto.totalPages
            response["totalPages"] = userChatRoomListDto.totalPages
            response["chatRooms"] = userChatRoomListDto.chatRooms
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to list all user's chat rooms: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all user's chat rooms: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }


    //listAllChatRoomMessages (with paging) -> chat of currentLoggedInUser with userId
    @GetMapping(value = "/{userId}/messages", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllChatRoomMessages(@PathVariable("userId") String userId,
                                @RequestParam(name = "page", defaultValue = "0") Integer page,
                                @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                @RequestParam(name = "order", defaultValue = "asc") String order,
                                Authentication authentication) {
        def response = [
                "success"         : true,
                "hasNextPage"     : false,
                "user"            : null,
                "chatRoomMessages": [],
                "totalPages"      : null,
                "error"           : ""
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

        try {
            def userChatRoomMessageListDto = userChatIntegrationService.findAllUserChatRoomMessages(userId.toLong(), page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userChatRoomMessageListDto.totalPages
            response["totalPages"] = userChatRoomMessageListDto.totalPages
            response["user"] = userChatRoomMessageListDto.user
            response["chatRoomMessages"] = userChatRoomMessageListDto.chatRoomMessages
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to list all user's messages with user with id '${userId}': ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all user's messages with user with id '${userId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    //listAllUnreadMessages (with paging)
}
