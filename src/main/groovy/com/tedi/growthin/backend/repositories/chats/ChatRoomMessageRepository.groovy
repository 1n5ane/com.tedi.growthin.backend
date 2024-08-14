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

    @Query("select crm from ChatRoomMessage crm where crm.chatRoom.id = :chatId")
    Page<ChatRoomMessage> findAllByChatRoomId(@Param("chatId") Long chatId, Pageable pageable)

}
