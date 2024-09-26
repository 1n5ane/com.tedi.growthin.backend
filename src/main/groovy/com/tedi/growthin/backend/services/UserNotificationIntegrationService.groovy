package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.notifications.Notification
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.notifications.NotificationDto
import com.tedi.growthin.backend.dtos.notifications.NotificationListDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.articles.UserArticleService
import com.tedi.growthin.backend.services.chats.ChatRoomService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.notifications.NotificationService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import groovy.util.logging.Slf4j
import org.apache.hc.core5.http.NotImplementedException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserNotificationIntegrationService {

    //if user is logged in notification service will notify user via socket (TODO: sockets for real time notifications)
    //if user is not logged in -> create notification entry in notification table
    //and when user logs in the app -> query the pending notifications from frontend.

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    NotificationService notificationService

    @Autowired
    UserConnectionService userConnectionService


    //TODO: refactor the following Autowired. (Should not exist here)
    //THE FOLLOWING AUTOWIRED ARE NOT MEANT TO BE HERE
    //These exists in order for things to work.. no time for refactor
    @Autowired
    UserArticleIntegrationService userArticleIntegrationService

    @Autowired
    UserChatIntegrationService userChatIntegrationService


    Boolean readAllUnreadNotifications(Boolean includeUnreadChatRoomNotification, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        Boolean success
        if (includeUnreadChatRoomNotification) {
            success = notificationService.readAllUnreadByRecipientId(currentLoggedInUserId)
        } else {
            success = notificationService.readAllUnreadByRecipientIdAndNotChatRoomNotificationType(currentLoggedInUserId)
        }

        return success
    }

    Boolean readAllUnreadByRecipientIdAndIdIn(List idList, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        return notificationService.readAllUnreadByRecipientIdAndIdIn(currentLoggedInUserId, idList)
    }


    //list all notifications where recipient is current logged in user
    NotificationListDto findAllUserNotifications(Boolean findChatRoomNotification,
                                                 Integer page,
                                                 Integer pageSize,
                                                 String sortBy,
                                                 String order,
                                                 Authentication authentication) throws Exception {
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

        def notificationPage
        if (findChatRoomNotification) {
            notificationPage = notificationService.findAllByRecipientId(currentLoggedInUserId, page, pageSize, sortBy, order)
        } else {
            notificationPage = notificationService.findAllByRecipientIdAndNotChatRoomNotificationType(currentLoggedInUserId, page, pageSize, sortBy, order)
        }

        NotificationListDto notificationListDto = new NotificationListDto()
        notificationListDto.totalPages = notificationPage.totalPages

        if (notificationPage.isEmpty()) {
            return notificationListDto
        }

        List<Notification> notificationList = notificationPage.getContent()

        def notificationDtos = []
        notificationList.each { n ->
            def notificationDto = NotificationService.notificationDtoFromNotification(n)
            //exclude recipient
            notificationDto.recipientDto = null
            notificationDtos.add(notificationDto)
        }

        notificationListDto.notifications = hidePrivateFieldsIfNotConnected(currentLoggedInUserId, notificationDtos)
        return notificationListDto
    }

    Long countAllUnreadUserNotifications(Boolean includeUnreadChatRoomNotification, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        def count
        if (includeUnreadChatRoomNotification) {
            count = notificationService.countAllUnreadByRecipientId(currentLoggedInUserId)
        } else {
            count = notificationService.countAllUnreadByRecipientIdAndNotChatRoomNotificationType(currentLoggedInUserId)
        }

        return count
    }

    Long countAllUnreadChatUserNotifications(Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        return notificationService.countAllUnreadChatRoomNotificationsByRecipientId(currentLoggedInUserId)
    }

    NotificationListDto findAllChatUserNotifications(Integer page,
                                                     Integer pageSize,
                                                     String sortBy,
                                                     String order,
                                                     Authentication authentication) throws Exception {
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

        def notificationPage = notificationService.findAllChatNotificationsByRecipientId(currentLoggedInUserId, page, pageSize, sortBy, order)

        NotificationListDto notificationListDto = new NotificationListDto()
        notificationListDto.totalPages = notificationPage.totalPages

        if (notificationPage.isEmpty()) {
            return notificationListDto
        }

        List<Notification> notificationList = notificationPage.getContent()

        def notificationDtos = []
        notificationList.each { n ->
            def notificationDto = NotificationService.notificationDtoFromNotification(n)
            //exclude recipient
            notificationDto.recipientDto = null
            notificationDtos.add(notificationDto)
        }

        notificationListDto.notifications = hidePrivateFieldsIfNotConnected(currentLoggedInUserId, notificationDtos)
        return notificationListDto
    }

    private List<NotificationDto> hidePrivateFieldsIfNotConnected(Long userId,
                                                                  List<NotificationDto> notificationDtos) throws Exception {

        //extract user ids contained in dto
        def userIds = []
        notificationDtos.each { notificationDto ->
            notificationDto.userDto ? userIds.add(notificationDto.userDto.id) : null
            notificationDto.recipientDto ? userIds.add(notificationDto.recipientDto.id) : null
        }

        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(userId, userIds)
        notificationDtos.each { notificationDto ->
            if (!connectedUserIds.contains(notificationDto.userDto?.id)) {
                notificationDto.userDto = UserDto.hidePrivateFields(notificationDto.userDto)
            }

            if (notificationDto.recipientDto != null && !connectedUserIds.contains(notificationDto.recipientDto.id)) {
                notificationDto.recipientDto = UserDto.hidePrivateFields(notificationDto.recipientDto)
            }

            if (notificationDto.isArticle) {

                notificationDto.articleDto = this.userArticleIntegrationService.hidePrivateUserFieldsOfNotConnectedUsers(userId, notificationDto.articleDto)

            } else if (notificationDto.isArticleReaction) {

                notificationDto.articleReactionDto = this.userArticleIntegrationService.hidePrivateUserFieldsOfArticleReactionsOfNotConnectedUsers(userId, [notificationDto.articleReactionDto]).first()

            } else if (notificationDto.isComment) {

                notificationDto.articleCommentDto = this.userArticleIntegrationService.hidePrivateUserFieldsOfNotConnectedUsers(userId, notificationDto.articleCommentDto)

            } else if (notificationDto.isCommentReaction) {

                notificationDto.articleCommentReactionDto = this.userArticleIntegrationService.hidePrivateUserFieldsOfCommentReactionsOfNotConnectedUsers(userId, [notificationDto.articleCommentReactionDto]).first()

            } else if (notificationDto.isConnectionRequest) {
                //nothing to hide
            } else if (notificationDto.isChat) {
                notificationDto.chatRoomDto = this.userChatIntegrationService.hidePrivateUserFieldsOfNotConnectedUsers(userId, notificationDto.chatRoomDto)
            } else {
                throw new NotImplementedException("All boolean fields of notificationDto are false! Maybe not implemented")
            }
        }
        return notificationDtos
    }
}
