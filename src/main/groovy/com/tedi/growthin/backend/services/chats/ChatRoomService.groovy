package com.tedi.growthin.backend.services.chats

import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.repositories.chats.ChatRoomRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.chats.ChatRoomException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class ChatRoomService {

    @Autowired
    MediaService mediaService

    @Autowired
    UserRepository userRepository

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
        return chatRoomDtoFromChatRoom(chatRoom)
    }

    static def chatRoomDtoFromChatRoom(ChatRoom chatRoom){

        return new ChatRoomDto(
                chatRoom.id,
                UserService.userDtoFromUser(chatRoom.user1),
                UserService.userDtoFromUser(chatRoom.user2),
                chatRoom.createdAt
        )

    }


}
