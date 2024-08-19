package com.tedi.growthin.backend.repositories.chats

import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomMessageRepository extends PagingAndSortingRepository<ChatRoomMessage, Long>, CrudRepository<ChatRoomMessage, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ChatRoomMessage crm set crm.isRead = :isRead where ((crm.chatRoom.user1.id = :senderId and crm.chatRoom.user2.id = :receiverId) or (crm.chatRoom.user2.id = :senderId and crm.chatRoom.user1.id = :receiverId)) and crm.id in :messageIds and crm.sender.id = :senderId")
    void setIsReadToMessages(@Param("isRead") Boolean isRead,
                             @Param("senderId") Long senderId,
                             @Param("receiverId") Long receiverId,
                             @Param("messageIds") List<Long> messageIds)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update ChatRoomMessage crm set crm.isRead = :isRead where crm.chatRoom.id = :chatId and crm.sender.id = :senderId and crm.isRead != :isRead")
    void setIsReadToAllChatRoomMessagesBySender(@Param("chatId") Long chatId, @Param("senderId") Long senderId, @Param("isRead") Boolean isRead)

    @Query("select crm from ChatRoomMessage crm where crm.chatRoom.id = :chatId")
    Page<ChatRoomMessage> findAllByChatRoomId(@Param("chatId") Long chatId, Pageable pageable)

    @Query("select crm from ChatRoomMessage crm where crm.chatRoom.id = :chatId and crm.id = :messageId")
    Optional<ChatRoomMessage> findByChatRoomIdAndMessageId(@Param("chatId") Long chatId, @Param("messageId") Long messageId)

    @Query("select count(1) from ChatRoomMessage crm where crm.chatRoom.id = :chatId and crm.sender.id = :senderId and crm.isRead = false")
    Long countUnreadChatRoomMessagesBySenderId(@Param("chatId") Long chatId, @Param("senderId") Long senderId)

    //sender != userId (userId is currentLoggedInUserId)

    @Query("select count(distinct crm.chatRoom.id) from ChatRoomMessage crm where (crm.chatRoom.user1.id = :userId or crm.chatRoom.user2.id = :userId) and crm.sender.id != :userId and crm.isRead = false")
    Long countChatRoomsWithUnreadMessagesFromSender(@Param("userId") Long userId)
}
