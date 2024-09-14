package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.services.UserNotificationIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

//A few words about notifications:
//
//this controller is used for listing users notifications
//it won't be used for posting notifications
//ex. if a user reacts to an article
//    notification will be handled by server
//    No need for user to manually post notification from frontend
//
//    The problem with this is that notificationService will be called inside
//    the particular transaction (ex. reacting to an article/comment)
//    This breaks the Single Responsibility from SOLID principles...
//    But for the sake of assignement we'll do it that way
//    (if notification fails or if the server stops inside the transaction,
//    user action will also be rolled back)

//For example if a user comments an article
//  in the comment insertion transaction -> notify will be sent
//  If comment insertion fails -> no notification
//  If notification fails (deployment of server or if the server stops)
//  comment won't be inserted and client will try again...

// this is for the sake of simplicity for the assignment
// In general if this was ment for production
// we would need a mechanism such as a message queue (never fails when client successfully publish message)
@RestController
@RequestMapping("/api/v1/notification")
@Slf4j
class UserNotificationController {

    @Autowired
    UserNotificationIntegrationService userNotificationIntegrationService

    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllNotifications(@RequestParam(name = "page", defaultValue = "0") Integer page,
                             @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                             @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                             @RequestParam(name = "order", defaultValue = "desc") String order,
                             Authentication authentication) {
        def response = [
                "success"      : true,
                "hasNextPage"  : false,
                "notifications": null,
                "totalPages"   : null,
                "error"        : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userNotificationListDto = userNotificationIntegrationService.findAllUserNotifications(false, page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userNotificationListDto.totalPages
            response["totalPages"] = userNotificationListDto.totalPages
            response["notifications"] = userNotificationListDto.notifications
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all user notifications: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = ["/count"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def countAllUnreadNotifications(@RequestParam(name="includeChatNotifications",defaultValue = "true") Boolean includeChatNotifications,Authentication authentication) {
        def response = [
                "success"      : true,
                "count"        : null,
                "error"        : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def count = userNotificationIntegrationService.countAllUnreadUserNotifications(includeChatNotifications, authentication)
            response["count"] = count
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to count all user action notifications: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = ["/chat/count"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def countAllUnreadChatNotifications(@RequestParam(name="includeChatNotifications",defaultValue = "true") Boolean includeChatNotifications,Authentication authentication) {
        def response = [
                "success"      : true,
                "count"        : null,
                "error"        : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def count = userNotificationIntegrationService.countAllUnreadChatUserNotifications(authentication)
            response["count"] = count
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to count all user chat notifications: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }



    @GetMapping(value = ["/chat"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllChatNotifications(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                 @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                 @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
                                 @RequestParam(name = "order", defaultValue = "desc") String order,
                                 Authentication authentication) {
        def response = [
                "success"      : true,
                "hasNextPage"  : false,
                "notifications": null,
                "totalPages"   : null,
                "error"        : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userNotificationListDto = userNotificationIntegrationService.findAllChatUserNotifications(page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userNotificationListDto.totalPages
            response["totalPages"] = userNotificationListDto.totalPages
            response["notifications"] = userNotificationListDto.notifications
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all user chat notifications: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }
}
