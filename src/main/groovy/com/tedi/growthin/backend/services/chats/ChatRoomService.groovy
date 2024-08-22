package com.tedi.growthin.backend.services.chats

import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessagesIsReadDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.notifications.NotificationDto
import com.tedi.growthin.backend.dtos.notifications.NotificationTypeDto
import com.tedi.growthin.backend.repositories.chats.ChatRoomMessageRepository
import com.tedi.growthin.backend.repositories.chats.ChatRoomRepository
import com.tedi.growthin.backend.repositories.users.UserConnectionRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.services.notifications.NotificationService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomMessageException
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class ChatRoomService {

    @Autowired
    UserRepository userRepository

    @Autowired
    UserConnectionRepository userConnectionRepository


    @Autowired
    MediaService mediaService

    @Autowired
    ChatRoomMessageRepository chatRoomMessageRepository

    @Autowired
    ChatRoomRepository chatRoomRepository

    @Autowired
    NotificationService notificationService

    Boolean checkChatRoomExistsByRelatedUsers(Long relatedUserId1, Long relatedUserId2) throws Exception {
        if (relatedUserId1 == null)
            throw new ChatRoomException("RelatedUser1 id can't be empty")

        if (relatedUserId2 == null)
            throw new ChatRoomException("RelatedUser2 id can't be empty")

        return chatRoomRepository.existsChatRoom(
                relatedUserId1,
                relatedUserId2
        )
    }

    Long countAllUnreadChatMessages(Long chatRoomId, Long senderId) throws Exception {
        if (senderId == null) {
            throw new ChatRoomMessageException("Sender id can't be empty")
        }

        if (chatRoomId == null) {
            throw new ChatRoomException("Chat room id can't be empty")
        }

        return chatRoomMessageRepository.countUnreadChatRoomMessagesBySenderId(chatRoomId, senderId)
    }

    Long countAllChatRoomsWithUnreadMessages(Long userId) throws Exception {
        if (userId == null) {
            throw new ChatRoomException("User id can't be empty")
        }

        return chatRoomMessageRepository.countChatRoomsWithUnreadMessagesFromSender(userId)
    }

    ChatRoomDto findChatRoomById(Long chatRoomId) throws Exception {

        if (chatRoomId == null)
            throw new ChatRoomException("Chat room id can't be empty")

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById(chatRoomId)
        if (optionalChatRoom.isPresent())
            return hidePrivateUserFieldsIfNotConnected(optionalChatRoom.get())

        return null
    }

    ChatRoomDto findChatRoomByRelatedUsers(Long relatedUserId1, Long relatedUserId2) throws Exception {
        if (relatedUserId1 == null)
            throw new ChatRoomException("RelatedUser1 id can't be empty")

        if (relatedUserId2 == null)
            throw new ChatRoomException("RelatedUser2 id can't be empty")

        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRelatedUsers(relatedUserId1, relatedUserId2)
        if (optionalChatRoom.isPresent())
            return hidePrivateUserFieldsIfNotConnected(optionalChatRoom.get())

        return null
    }

    @Transactional(rollbackFor = Exception.class)
    ChatRoomDto createChatRoom(ChatRoomDto chatRoomDto) throws Exception {
        Optional<User> optionalUser1 = userRepository.findById((Long) chatRoomDto.relatedUser1.id)
        if (optionalUser1.isEmpty())
            throw new ChatRoomException("User with id '${chatRoomDto.relatedUser1.id}' was not found")

        Optional<User> optionalUser2 = userRepository.findById((Long) chatRoomDto.relatedUser2.id)
        if (optionalUser2.isEmpty())
            throw new ChatRoomException("User with id '${chatRoomDto.relatedUser2.id}' was not found")

        ChatRoom chatRoom = new ChatRoom(null, optionalUser1.get(), optionalUser2.get())

        chatRoom = chatRoomRepository.save(chatRoom)
        return hidePrivateUserFieldsIfNotConnected(chatRoom)
    }

    @Transactional(rollbackFor = Exception.class)
    ChatRoomMessageDto createChatRoomMessage(ChatRoomMessageDto chatRoomMessageDto, Boolean notify = true) throws Exception {
        //first check if users exist
        Optional<User> optionalUser1 = userRepository.findById((Long) chatRoomMessageDto.senderId)
        if (optionalUser1.isEmpty())
            throw new ChatRoomMessageException("User with id '${chatRoomMessageDto.senderId}' was not found")

        Optional<User> optionalUser2 = userRepository.findById((Long) chatRoomMessageDto.receiverId)
        if (optionalUser2.isEmpty())
            throw new ChatRoomMessageException("User with id '${chatRoomMessageDto.receiverId}' was not found")

        //check chatroom exists for these 2 users
        Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRelatedUsers(
                (Long) chatRoomMessageDto.senderId, (Long) chatRoomMessageDto.receiverId
        )

        if (optionalChatRoom.isEmpty())
            throw new ChatRoomMessageException("Chat room for users with ids '${chatRoomMessageDto.senderId}', '${chatRoomMessageDto.receiverId}' was not found")

        ChatRoom userChatRoom = optionalChatRoom.get()

        //if media provided -> create it
        Media media = null
        if (chatRoomMessageDto.mediaDto)
            media = mediaService.createMedia(chatRoomMessageDto.mediaDto)

        //all good sofar -> create new message
        ChatRoomMessage chatRoomMessage = new ChatRoomMessage(
                null,
                userChatRoom,
                optionalUser1.get(),
                chatRoomMessageDto.message,
                false,
                media
        )

        chatRoomMessage = chatRoomMessageRepository.save(chatRoomMessage)
        def createdChatRoomMessageDto = chatRoomMessageDtoFromChatRoomMessage(chatRoomMessage)


        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        if (notify) {
            //notify receiver of message for the new message
            def notificationDto = new NotificationDto(
                    null,
                    UserService.userDtoFromUser(chatRoomMessage.sender),
                    UserService.userDtoFromUser(optionalUser2.get()),//opitonalUser2 is receiver of message (so receiver of notification)
                    new NotificationTypeDto(4, "CHAT_ROOM"),
                    true,
                    chatRoomDtoFromChatRoom(userChatRoom),
                    false
            )
            notificationService.createNotification(notificationDto)
            log.info("Successfully created notification for chat room message to user")
        }


        return createdChatRoomMessageDto
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean setIsReadToAllChatRoomMessagesBySender(Long chatId, Long senderId, Boolean isRead) throws Exception {
        chatRoomMessageRepository.setIsReadToAllChatRoomMessagesBySender(chatId, senderId, isRead)
        return true
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean setMessagesIsRead(ChatRoomMessagesIsReadDto chatRoomMessagesIsReadDto) throws Exception {
        Optional<User> optionalUser1 = userRepository.findById((Long) chatRoomMessagesIsReadDto.senderId)
        if (optionalUser1.isEmpty())
            throw new ChatRoomException("User with id '${chatRoomMessagesIsReadDto.senderId}' was not found")

        Optional<User> optionalUser2 = userRepository.findById((Long) chatRoomMessagesIsReadDto.receiverId)
        if (optionalUser2.isEmpty())
            throw new ChatRoomException("User with id '${chatRoomMessagesIsReadDto.receiverId}' was not found")

        chatRoomMessageRepository.setIsReadToMessages(
                chatRoomMessagesIsReadDto.isRead,
                (Long) chatRoomMessagesIsReadDto.senderId,
                (Long) chatRoomMessagesIsReadDto.receiverId,
                chatRoomMessagesIsReadDto.messageIds
        )

        return true
    }

    Page<ChatRoom> listAllChatRooms(Long userId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        //check if userId exists
        def optionalUser = userRepository.findById(userId)

        if (optionalUser.isEmpty()) {
            throw new ChatRoomException("User reference id '${userId}' not found")
        }

        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<ChatRoom> pageChatRoom = chatRoomRepository.findAllByUserId(userId, pageable)

        return pageChatRoom
    }

    Page<ChatRoomMessage> listAllChatRoomMessages(Long chatId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        //check if chatId exists
        Boolean chatRoomExists = chatRoomRepository.existsChatRoom(chatId)

        if (!chatRoomExists) {
            throw new ChatRoomMessageException("Chat room reference id '${chatId}' not found")
        }

        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<ChatRoomMessage> pageChatRoomMessage = chatRoomMessageRepository.findAllByChatRoomId(chatId, pageable)
        return pageChatRoomMessage
    }

    ChatRoomMessageDto findChatRoomMessage(ChatRoomMessageDto chatRoomMessageDto) throws Exception {
        def chatRoom
        if (chatRoomMessageDto.chatRoomId == null) {
            //if chatRoomId not provided -> find chatroom by related users
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findByRelatedUsers(
                    (Long) chatRoomMessageDto.senderId, (Long) chatRoomMessageDto.receiverId
            )
            if (optionalChatRoom.isEmpty())
                throw new ChatRoomMessageException("Chat room for users with ids '${chatRoomMessageDto.senderId}', '${chatRoomMessageDto.receiverId}' was not found")

            chatRoom = optionalChatRoom.get()
        } else {
            //if chatRoomId provided -> get with chatRoomId
            Optional<ChatRoom> optionalChatRoom = chatRoomRepository.findById((Long) chatRoomMessageDto.chatRoomId)
            if (optionalChatRoom.isEmpty())
                throw new ChatRoomMessageException("Chat room with id '${chatRoomMessageDto.chatRoomId}' was not found")
            chatRoom = optionalChatRoom.get()
            def userIds = []
            userIds.add(chatRoom.user1.id)
            userIds.add(chatRoom.user2.id)
            if (!userIds.contains(chatRoomMessageDto.senderId) && !userIds.contains(chatRoomMessageDto.receiverId)) {
                throw new ForbiddenException("Chat room not related to users with ids '${chatRoomMessageDto.senderId}', '${chatRoomMessageDto.receiverId}'")
            }
        }

        Optional<ChatRoomMessage> optionalChatRoomMessage = chatRoomMessageRepository.findByChatRoomIdAndMessageId(chatRoom.id, (Long) chatRoomMessageDto.id)
        if (optionalChatRoomMessage.isEmpty())
            return null
        return chatRoomMessageDtoFromChatRoomMessage(optionalChatRoomMessage.get())
    }


    private ChatRoomDto hidePrivateUserFieldsIfNotConnected(ChatRoom chatRoom) throws Exception {
        Boolean isConnected = true

        //if user not chatting with himself
        if (chatRoom.user1.id != chatRoom.user2.id) {
            //check if users are connected and hide private fields in case they are not
            isConnected = userConnectionRepository.existsUserConnection(chatRoom.user1.id, chatRoom.user2.id)
        }

        def newChatRoomDto
        if (!isConnected) {
            newChatRoomDto = chatRoomDtoFromChatRoomWithHiddenPrivateUserFields(chatRoom, false, true)
        } else {
            newChatRoomDto = chatRoomDtoFromChatRoom(chatRoom)
        }
        return newChatRoomDto
    }

    static def chatRoomMessageDtoFromChatRoomMessage(ChatRoomMessage chatRoomMessage) {
        return new ChatRoomMessageDto(
                chatRoomMessage.id,
                chatRoomMessage.chatRoom.id,
                chatRoomMessage.sender.id,
                chatRoomMessage.sender.id == chatRoomMessage.chatRoom.user1.id ? chatRoomMessage.chatRoom.user2.id : chatRoomMessage.chatRoom.user1.id,
                chatRoomMessage.message,
                chatRoomMessage.isRead,
                chatRoomMessage.media ? MediaService.mediaDtoFromMedia(chatRoomMessage.media) : null,
                chatRoomMessage.createdAt,
                chatRoomMessage.updatedAt
        )
    }

    static def chatRoomDtoFromChatRoom(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.id,
                chatRoom.user1 ? UserService.userDtoFromUser(chatRoom.user1) : null,
                chatRoom.user2 ? UserService.userDtoFromUser(chatRoom.user2) : null,
                chatRoom.createdAt
        )
    }

    static def chatRoomDtoFromChatRoomWithHiddenPrivateUserFields(ChatRoom chatRoom, Boolean hideUser1 = true, Boolean hideUser2 = true) {
        return new ChatRoomDto(
                chatRoom.id,
                hideUser1 ? UserService.userDtoFromUserWithHiddenPrivateFields(chatRoom.user1) : UserService.userDtoFromUser(chatRoom.user1),
                hideUser2 ? UserService.userDtoFromUserWithHiddenPrivateFields(chatRoom.user2) : UserService.userDtoFromUser(chatRoom.user2),
                chatRoom.createdAt
        )
    }


}
