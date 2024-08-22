package com.tedi.growthin.backend.repositories.notifications

import com.tedi.growthin.backend.domains.notifications.Notification
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    Page<Notification> findAllByRecipientIdAndNotChatRoomNotificationType(@Param("recipientId") Long recipientId, Pageable pageable)

}
