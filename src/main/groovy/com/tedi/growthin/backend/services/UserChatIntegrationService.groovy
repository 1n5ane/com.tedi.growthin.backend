package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomListDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageListDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessagesIsReadDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.chats.ChatRoomService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomMessageException
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

//TODO:Implement notification logic -> will be used here
//     notify user for incoming message if connected with web sockets.

@Service
@Slf4j
class UserChatIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserConnectionService userConnectionService

    @Autowired
    UserService userService

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
        if (chatRoomMessageDto.chatRoomId == null) {
            //check if chat with these 2 users exists
            ChatRoomDto chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(
                    (Long) chatRoomMessageDto.senderId,
                    (Long) chatRoomMessageDto.receiverId
            )

            if (chatRoomDto == null) {
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

    Boolean readAllUnreadChatRoomMessages(Long userId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        ChatRoomDto chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(
                currentLoggedInUserId,
                userId
        )

        if (chatRoomDto == null) {
            throw new ChatRoomException("Chat room not exists for users with ids '${currentLoggedInUserId}', '${userId}'")
        }

        return chatRoomService.setIsReadToAllChatRoomMessagesBySender((Long) chatRoomDto.id, userId, true)
    }

    //set messageList isRead
    Boolean setIsReadToMessages(ChatRoomMessagesIsReadDto chatRoomMessagesIsReadDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        chatRoomMessagesIsReadDto.receiverId = currentLoggedInUserId

        validationServiceMap["chatRoomMessageIsReadValidationService"].validate(chatRoomMessagesIsReadDto)

        ChatRoomDto chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(
                (Long) chatRoomMessagesIsReadDto.senderId,
                (Long) chatRoomMessagesIsReadDto.receiverId
        )

        if (chatRoomDto == null) {
            throw new ChatRoomException("Chat room not exists for users with ids '${chatRoomMessagesIsReadDto.senderId}', '${chatRoomMessagesIsReadDto.receiverId}'")
        }

        chatRoomMessagesIsReadDto.messageIds = (chatRoomMessagesIsReadDto.messageIds as List<String>).collect { it.toLong() }
        chatRoomService.setMessagesIsRead(chatRoomMessagesIsReadDto)
        return true
    }

    ChatRoomListDto findAllUserChatRooms(Integer page, Integer pageSize, String sortBy, String order, Authentication authentication) throws Exception {
        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "createdAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt]")

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        Page<ChatRoom> pageChatRoom = chatRoomService.listAllChatRooms(currentLoggedInUserId, page, pageSize, sortBy, order)

        ChatRoomListDto chatRoomListDto = new ChatRoomListDto()
        chatRoomListDto.totalPages = pageChatRoom.totalPages

        if (pageChatRoom.isEmpty())
            return chatRoomListDto

        List<ChatRoom> chatRooms = pageChatRoom.getContent()
        //hide private user fields if not connected with chat user
        def chatUserIds = []

        //get users ids that currentLoggedIn user chats with
        chatRooms.each { cr ->
            chatUserIds.add(currentLoggedInUserId == cr.user1.id ? cr.user2.id : cr.user1.id)
        }

        def connectedChatUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, chatUserIds)

        chatRooms.each { cr ->
            chatRoomListDto.chatRooms.add([
                    "id"         : cr.id,
                    "user"       : currentLoggedInUserId == cr.user1.id ?
                            (connectedChatUserIds.contains(cr.user2.id) || currentLoggedInUserId == cr.user2.id ? UserService.userDtoFromUser(cr.user2) : UserService.userDtoFromUserWithHiddenPrivateFields(cr.user2))
                            : (connectedChatUserIds.contains(cr.user1.id) ? UserService.userDtoFromUser(cr.user1) : UserService.userDtoFromUserWithHiddenPrivateFields(cr.user1)),
                    "lastMessage": cr.chatRoomMessages ? ChatRoomService.chatRoomMessageDtoFromChatRoomMessage(cr.chatRoomMessages.first()) : null,//chatRoomMessages contains last 2 messages order by id dec (so first get the latest)
                    "createdAt"  : cr.createdAt
            ])
        }
        return chatRoomListDto
    }

    //listAllChatRoomMessages currentLoggedInUser with userId(with paging)
    ChatRoomMessageListDto findAllUserChatRoomMessages(Long userId, Integer page, Integer pageSize, String sortBy, String order, Authentication authentication) throws Exception {
        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "createdAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt]")

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check if chat room exists
        def chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(currentLoggedInUserId, userId)

        if (chatRoomDto == null) {
            throw new ChatRoomMessageException("Chat room not exists for users with ids '${currentLoggedInUserId}', '${userId}'")
        }

        Page<ChatRoomMessage> pageChatRoomMessage = chatRoomService.listAllChatRoomMessages((Long) chatRoomDto.id, page, pageSize, sortBy, order)

        def userConnectionDto = new UserConnectionDto(null, currentLoggedInUserId, userId)
        //check if currentLoggedInUser is connected with userId
        Boolean isConnected = userConnectionService.checkUserConnectionExists(userConnectionDto)

        ChatRoomMessageListDto chatRoomMessageListDto = new ChatRoomMessageListDto()
        chatRoomMessageListDto.totalPages = pageChatRoomMessage.totalPages

        if (isConnected) {
            chatRoomMessageListDto.user = userService.getUserById(userId)
        } else {
            chatRoomMessageListDto.user = UserDto.hidePrivateFields(userService.getUserById(userId))
        }

        if (pageChatRoomMessage.isEmpty()) {
            return chatRoomMessageListDto
        }

        List<ChatRoomMessage> chatRoomMessages = pageChatRoomMessage.getContent()
        chatRoomMessages.each { crm ->
            chatRoomMessageListDto.chatRoomMessages.add([
                    "id"       : crm.id,
                    "senderId" : crm.sender.id,
                    "message"  : crm.message,
                    "media"    : crm.media ? MediaService.mediaDtoFromMedia(crm.media) : null,
                    "isRead"   : crm.isRead,
                    "createdAt": crm.createdAt
            ])
        }

        return chatRoomMessageListDto
    }

    ChatRoomMessageDto findUserChatRoomMessage(ChatRoomMessageDto chatRoomMessageDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        chatRoomMessageDto.senderId = currentLoggedInUserId
        //check if chat room exists
        def chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(currentLoggedInUserId, (Long) chatRoomMessageDto.receiverId)

        if (chatRoomDto == null) {
            throw new ChatRoomMessageException("Chat room not exists for users with ids '${currentLoggedInUserId}', '${chatRoomMessageDto.receiverId}'")
        }

        chatRoomMessageDto.chatRoomId = chatRoomDto.id

        return chatRoomService.findChatRoomMessage(chatRoomMessageDto)
    }

    Long countAllUnreadChatMessages(Long userId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        ChatRoomDto chatRoomDto = chatRoomService.findChatRoomByRelatedUsers(
                userId,
                currentLoggedInUserId
        )

        if (chatRoomDto == null) {
            throw new ChatRoomException("Chat room not exists for users with ids '${userId}', '${currentLoggedInUserId}'")
        }

        return chatRoomService.countAllUnreadChatMessages((Long) chatRoomDto.id, userId)
    }

    Long countAllChatRoomsWithUnreadMessages(Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //count all chhat rooms with unread with receiver currentLoggedInUser
        return chatRoomService.countAllChatRoomsWithUnreadMessages(currentLoggedInUserId)
    }

}
