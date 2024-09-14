package com.tedi.growthin.backend.services.notifications

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.articles.ArticleCommentReaction
import com.tedi.growthin.backend.domains.articles.ArticleReaction
import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.notifications.Notification
import com.tedi.growthin.backend.domains.notifications.NotificationType
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.notifications.NotificationDto
import com.tedi.growthin.backend.dtos.notifications.NotificationTypeDto
import com.tedi.growthin.backend.repositories.notifications.NotificationRepository
import com.tedi.growthin.backend.services.articles.UserArticleService
import com.tedi.growthin.backend.services.chats.ChatRoomService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.notifications.NotificationException
import groovy.util.logging.Slf4j
import org.apache.hc.core5.http.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Slf4j
class NotificationService {

    @Autowired
    NotificationRepository notificationRepository

    Page<Notification> findAllByRecipientIdAndNotChatRoomNotificationType(Long recipientId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<Notification> pageNotification = notificationRepository.findAllByRecipientIdAndNotChatRoomNotificationType(recipientId, pageable)
        return pageNotification
    }

    Page<Notification> findAllByRecipientId(Long recipientId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<Notification> pageNotification = notificationRepository.findAllByRecipientId(recipientId, pageable)
        return pageNotification
    }

    Long countAllUnreadByRecipientId(Long recipientId) throws Exception {
        return notificationRepository.countAllUnreadByRecipientId(recipientId)
    }

    Long countAllUnreadChatRoomNotificationsByRecipientId(Long recipientId) throws Exception {
        return notificationRepository.countAllUnreadChatRoomNotificationsByRecipientId(recipientId)
    }

    Long countAllUnreadByRecipientIdAndNotChatRoomNotificationType(Long recipientId) throws Exception {
        return notificationRepository.countAllUnreadByRecipientIdAndNotChatRoomNotificationType(recipientId)
    }

    Page<Notification> findAllChatNotificationsByRecipientId(Long recipientId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))
        Page<Notification> pageNotification = notificationRepository.findAllChatNotificationsByRecipientId(recipientId, pageable)
        return pageNotification
    }

    @Transactional(rollbackFor = Exception.class)
    NotificationDto createNotification(NotificationDto notificationDto) throws Exception {
        if (notificationDto.userDto == null) throw new NotificationException("User can't be empty for notification")
        if (notificationDto.recipientDto == null) throw new NotificationException("Recipient can't be empty for notification")
        if (notificationDto.notificationTypeDto == null) throw new NotificationException("Notification type can't be empty for notification")

        def notificationEntity
        if (notificationDto.isArticle && notificationDto.articleDto) {
            notificationEntity = new Article()
            notificationEntity.id = notificationDto.articleDto.id
        } else if (notificationDto.isArticleReaction && notificationDto.articleReactionDto) {
            notificationEntity = new ArticleReaction()
            notificationEntity.id = notificationDto.articleReactionDto.id
        } else if (notificationDto.isComment && notificationDto.articleCommentDto) {
            notificationEntity = new ArticleComment()
            notificationEntity.id = notificationDto.articleCommentDto.id
        } else if (notificationDto.isCommentReaction && notificationDto.articleCommentReactionDto) {
            notificationEntity = new ArticleCommentReaction()
            notificationEntity.id = notificationDto.articleCommentReactionDto.id
        } else if (notificationDto.isConnectionRequest && notificationDto.userConnectionRequestDto) {
            notificationEntity = new UserConnectionRequest()
            notificationEntity.id = notificationDto.userConnectionRequestDto.id
        } else if (notificationDto.isChat && notificationDto.chatRoomDto) {
            notificationEntity = new ChatRoom()
            notificationEntity.id = notificationDto.chatRoomDto.id
        } else {
            throw new NotImplementedException("All boolean fields of notificationDto are false! Maybe not implemented. Notification not created!")
        }

        Notification notification = new Notification(
                UserService.userFromUserDto(notificationDto.userDto),
                UserService.userFromUserDto(notificationDto.recipientDto),
                new NotificationType((Integer) notificationDto.notificationTypeDto.id, notificationDto.notificationTypeDto.name),
                true,
                notificationEntity,
                false
        )

        notification = notificationRepository.save(notification)

        return notificationDtoFromNotification(notification)
    }

    static NotificationTypeDto notificationTypeDtoFromNotificationType(NotificationType notificationType) {
        return new NotificationTypeDto(notificationType.id, notificationType.name)
    }

    static NotificationDto notificationDtoFromNotification(Notification notification) {
        def notificationDto
        if (notification.isArticle) {
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isArticle,
                    UserArticleService.articleDtoFromArticle(notification.article),
                    notification.viewed,
                    notification.createdAt
            )
        } else if (notification.isArticleReaction) {
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isArticleReaction,
                    notification.articleReaction ? UserArticleService.articleReactionDtoFromArticleReaction(notification.articleReaction) : null,
                    notification.viewed,
                    notification.createdAt
            )
        } else if (notification.isComment) {
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isComment,
                    UserArticleService.articleCommentDtoFromArticleComment(notification.comment),
                    notification.viewed,
                    notification.createdAt
            )
        } else if (notification.isCommentReaction) {
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isCommentReaction,
                    notification.commentReaction ? UserArticleService.articleCommentReactionDtoFromArticleCommentReaction(notification.commentReaction) : null,
                    notification.viewed,
                    notification.createdAt
            )
        } else if (notification.isConnectionRequest) {
            def userConnectionRequestDto = new UserConnectionRequestDto(
                    notification.connectionRequest.id,
                    notification.connectionRequest.user?.id,
                    notification.connectionRequest.connectedUser?.id,
                    notification.connectionRequest.status,
                    notification.connectionRequest.createdAt,
                    notification.connectionRequest.updatedAt
            )
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isConnectionRequest,
                    userConnectionRequestDto,
                    notification.viewed,
                    notification.createdAt
            )
        } else if (notification.isChat) {
            notificationDto = new NotificationDto(
                    notification.id,
                    notification.user ? UserService.userDtoFromUser(notification.user) : null,
                    notification.recipient ? UserService.userDtoFromUser(notification.recipient) : null,
                    notificationTypeDtoFromNotificationType(notification.notificationType),
                    notification.isChat,
                    notification.chatRoom ? ChatRoomService.chatRoomDtoFromChatRoom(notification.chatRoom) : null,
                    notification.viewed,
                    notification.createdAt
            )
        } else {
            throw new NotImplementedException("All boolean fields of notificationDto are false! Maybe not implemented")
        }

        return notificationDto
    }

    @Transactional(rollbackFor = Exception.class)
    void deleteConnectionNotification(Long connectionRequestId){
        notificationRepository.deleteByConnectionRequestId(connectionRequestId)
    }
}
