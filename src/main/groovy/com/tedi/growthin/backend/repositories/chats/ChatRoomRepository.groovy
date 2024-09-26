package com.tedi.growthin.backend.repositories.chats

import com.tedi.growthin.backend.domains.chat.ChatRoom
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository extends PagingAndSortingRepository<ChatRoom, Long>, CrudRepository<ChatRoom, Long> {


    @Query("select case when count(cr) > 0 then true else false end from ChatRoom cr where (cr.user1.id = :userId1 and cr.user2.id = :userId2) or (cr.user1.id = :userId2 and cr.user2.id = :userId1)")
    Boolean existsChatRoom(@Param("userId1") Long userId1, @Param("userId2") Long userId2)

    @Query("select case when count(cr) > 0 then true else false end from ChatRoom cr where cr.id = :chatId ")
    Boolean existsChatRoom(@Param("chatId") Long chatId)

    @Query("select cr from ChatRoom cr where (cr.user1.id = :userId1 and cr.user2.id = :userId2) or (cr.user1.id = :userId2 and cr.user2.id = :userId1)")
    Optional<ChatRoom> findByRelatedUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2)

    @Query("select cr from ChatRoom cr where cr.user1.id = :userId or cr.user2.id = :userId")
    Page<ChatRoom> findAllByUserId(@Param("userId")Long userId, Pageable pageable)

    @Query("select cr from ChatRoom cr where cr.user1.id in :userIds or cr.user2.id in :userIds")
    List<ChatRoom> findAllByUserIds(@Param("userIds") List<Long> userIds)

}