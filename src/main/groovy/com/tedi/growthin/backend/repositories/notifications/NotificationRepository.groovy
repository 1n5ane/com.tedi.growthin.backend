package com.tedi.growthin.backend.repositories.notifications

import com.tedi.growthin.backend.domains.notifications.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository extends PagingAndSortingRepository<Notification, Long>, CrudRepository<Notification, Long> {

    @Query("select n from Notification n where n.recipient.id = :recipientId")
    Page<Notification> findAllByRecipientId(@Param("recipientId") Long recipientId, Pageable pageable)

    @Query("select n from Notification n where n.recipient.id = :recipientId and n.notificationType.name = 'CHAT_ROOM'")
    Page<Notification> findAllChatNotificationsByRecipientId(@Param("recipientId") Long recipientId, Pageable pageable)

    @Query("select n from Notification n where n.recipient.id = :recipientId and n.notificationType.name <> 'CHAT_ROOM'")
    Page<Notification> findAllByRecipientIdAdNotChatRoomNotificationType(@Param("recipientId") Long recipientId, Pageable pageable)

    @Query("select count(1) from Notification n where n.recipient.id = :recipientId and n.viewed = false")
    Long countAllUnreadByRecipientId(@Param("recipientId") Long recipientId)

    @Query("select count(1) from Notification n where n.recipient.id = :recipientId and n.notificationType.name = 'CHAT_ROOM'")
    Long countAllUnreadChatRoomNotificationsByRecipientId(@Param("recipientId") Long recipientId)

    @Query("select count(1) from Notification n where n.recipient.id = :recipientId and n.notificationType.name <> 'CHAT_ROOM' and n.viewed = false")
    Long countAllUnreadByRecipientIdAndNotChatRoomNotificationType (@Param("recipientId") Long recipientId)

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete Notification n where n.connectionRequest.id = :connectionRequestId")
    void deleteByConnectionRequestId(@Param("connectionRequestId") Long connectionRequestId)

}
