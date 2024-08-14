package com.tedi.growthin.backend.services.chats

import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.repositories.chats.ChatRoomMessageRepository
import com.tedi.growthin.backend.repositories.chats.ChatRoomRepository
import com.tedi.growthin.backend.repositories.users.UserConnectionRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomMessageException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
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
    ChatRoomMessageDto createChatRoomMessage(ChatRoomMessageDto chatRoomMessageDto) throws Exception {
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
        return chatRoomMessageDtoFromChatRoomMessage(chatRoomMessage)
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
                chatRoomMessage.media?MediaService.mediaDtoFromMedia(chatRoomMessage.media):null,
                chatRoomMessage.createdAt,
                chatRoomMessage.updatedAt
        )
    }

    static def chatRoomDtoFromChatRoom(ChatRoom chatRoom) {
        return new ChatRoomDto(
                chatRoom.id,
                UserService.userDtoFromUser(chatRoom.user1),
                UserService.userDtoFromUser(chatRoom.user2),
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
