package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.admins.AdminRequestDto
import com.tedi.growthin.backend.dtos.admins.AdminRequestListDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.dtos.users.UserListDto
import com.tedi.growthin.backend.services.admins.articles.AdminArticleService
import com.tedi.growthin.backend.services.admins.chats.AdminChatRoomService
import com.tedi.growthin.backend.services.admins.users.AdminUserConnectionService
import com.tedi.growthin.backend.services.admins.users.AdminUserProfileService
import com.tedi.growthin.backend.services.admins.users.AdminUserService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class AdminIntegrationService extends UserIntegrationService {

    @Autowired
    AdminUserService adminUserService

    @Autowired
    AdminArticleService adminArticleService

    @Autowired
    AdminUserProfileService adminUserProfileService

    @Autowired
    AdminUserConnectionService adminUserConnectionService

    @Autowired
    AdminChatRoomService adminChatRoomService

    @Override
    UserDto getUser(UserDto userDto, Authentication authentication) throws Exception {
        UserDto user
        if (userDto.id != null) {
            user = adminUserService.getUserById((Long) userDto.id)
        } else if (userDto.email != null && !userDto.email.isEmpty()) {
            user = adminUserService.getUserByEmail(userDto.email)
        } else if (userDto.username != null && !userDto.username.isEmpty()) {
            user = adminUserService.getUserByUsername(userDto.username)
        } else {
            throw new UserValidationException("Please provide user id, email or username")
        }

        return user
    }

    UserListDto findAllRestrictedUsers(Integer page, Integer pageSize, String sortBy, String order, Boolean restricted = true) throws Exception {
        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "username", "firstName", "lastName", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, username, firstName, lastName, createdAt, updatedAt]")

        //get UserPage from service
        Page<User> pageUser = adminUserService.listAllRestrictedUsers(page, pageSize, sortBy, order, restricted)

        UserListDto userListDto = new UserListDto()
        userListDto.totalPages = pageUser.totalPages

        if (pageUser.isEmpty()) {
            return userListDto
        }

        List<User> userList = pageUser.getContent()

        userList.each { u ->
            userListDto.users.add(UserService.userDtoFromUser(u))
        }

        return userListDto
    }

    @Override
    UserListDto findAllUsers(Integer page, Integer pageSize, String sortBy, String order, Authentication authentication) throws Exception {

        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "username", "firstName", "lastName", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, username, firstName, lastName, createdAt, updatedAt]")

        //get UserPage from service
        Page<User> pageUser = adminUserService.listAllUsers(page, pageSize, sortBy, order)

        UserListDto userListDto = new UserListDto()
        userListDto.totalPages = pageUser.totalPages

        if (pageUser.isEmpty()) {
            return userListDto
        }

        List<User> userList = pageUser.getContent()

        userList.each { u ->
            userListDto.users.add(UserService.userDtoFromUser(u))
        }

        return userListDto
    }

    def updateUserLockedByIdList(List<Long> userIdList, Authentication authentication, Boolean lock) throws Exception {
        userIdList.each(id -> {
            def userDto = new UserDto()
            userDto.id = id
            this.updateUserLocked(userDto, authentication, lock)
        })
    }

    def updateUserLocked(UserDto userDto, Authentication authentication, Boolean lock) throws Exception {
        def user = this.getUser(userDto, authentication)

        if (user == null) {
            def providedField
            if (userDto.id != null) {
                providedField = userDto.id
            } else if (userDto.username != null) {
                providedField = userDto.username
            } else {
                providedField = userDto.email
            }
            throw new UserValidationException("User '${providedField}' was not found")
        }

        def userJwtToken = (Jwt) authentication.getCredentials()
        user["locked"] = lock
        def updatedUserDto = adminUserService.updateUser(user, userJwtToken.tokenValue)
        return updatedUserDto
    }

    AdminRequestListDto findAllAdminRequestsByStatus(AdminRequestStatus status,
                                                     Integer page,
                                                     Integer pageSize,
                                                     String sortBy,
                                                     String order,
                                                     Authentication authentication) throws Exception {
        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order.trim()
        ])

        //validate sortBy here
        sortBy = sortBy.trim()
        if (!["id", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt, updatedAt]")

//        def userJwtToken = (Jwt) authentication.getCredentials()
//        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)


        def userAdminRequestPage = adminUserService.listAllAdminUserRequestsByStatus(
                status,
                page,
                pageSize,
                sortBy,
                order.trim()
        )

        AdminRequestListDto adminRequestListDto = new AdminRequestListDto()

        adminRequestListDto.totalPages = userAdminRequestPage.totalPages

        if (userAdminRequestPage.isEmpty()) {
            return adminRequestListDto
        }

        //fill user admin requests

        List<UserAdminRequest> userAdminRequestList = userAdminRequestPage.getContent()

        userAdminRequestList.each { uar ->
            adminRequestListDto.requests.add([
                    "requestId": uar.id,
                    "user"     : UserService.userDtoFromUser(uar.user),
                    "curatedBy": uar.curatedByAdmin ? UserService.userDtoFromUser(uar.curatedByAdmin) : null,
                    "createdAt": uar.createdAt,
                    "updatedAt": uar.updatedAt
            ])
        }

        return adminRequestListDto

    }

    def updateUserAdminRequest(AdminRequestDto adminRequestDto, Authentication authentication) throws Exception {

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInAdminId = JwtService.extractAppUserId(userJwtToken)

        adminRequestDto.curatedByAdminId = currentLoggedInAdminId

        //also grants privileges in case admin request is accepted
        def updatedAdminRequest = adminUserService.updateAdminUserRequest(adminRequestDto, userJwtToken.tokenValue)
        return updatedAdminRequest
    }

    def exportUsersDataByUserIdList(List<Long> userIds, Authentication authentication) throws Exception {
        def exportData = [:]
        //each user id will be key of the map
        //values of each key will be articles(users articles), comments(users comments) , etc...

        // initialize with empty maps
        userIds.each(userId ->
                exportData[userId] = [
                        "userDetails"       : [:],
                        "articles"          : [],
                        "articleReactions"  : [],
                        "profile"           : [:],
                        "comments"          : [],
                        "commentReactions"  : [],
                        "connectionRequests": [],
                        "connections"       : [],
                        "chats"             : []
                ]
        )

        //fetch all user details for userIds list
        def userDtoList = adminUserService.findAllUsersByUserIds(userIds)
        userDtoList.each { userDto ->
            def userId = userDto.id as Long
            userDto.profilePic = null // will be contained in profile
            exportData[userId]['userDetails'] = userDto
        }
        userDtoList = null //garbage collect please

        //fetch all user profiles for userIds list
        def userProfileDtoList = adminUserProfileService.findAllByUserIds(userIds)
        userProfileDtoList.each { userProfileDto ->
            def userId = userProfileDto.id as Long//user id == profile id always
            exportData[userId]['profile'] = userProfileDto
        }
        userProfileDtoList = null


        //fetch all article for userIds list
        def articleDtoList = adminArticleService.listAllByUserIds(userIds)
        //assign each article to its respective user
        articleDtoList.each { articleDto ->
            def userId = articleDto.userDto.id as Long
            articleDto.userDto = null //we don't need user details (they are already included in export)
            articleDto.articleComments = null
            articleDto.articleReactions = null
            (exportData[userId]['articles'] as List).add(articleDto)
        }
        articleDtoList = null //garbage collect please

        //fetch all article reactions users have made
        def articleReactionsDtoList = adminArticleService.listAllArticleReactionsByUserIds(userIds)
        articleReactionsDtoList.each { articleReactionDto ->
            def userId = articleReactionDto.userDto.id as Long
            articleReactionDto.userDto = null
            (exportData[userId]['articleReactions'] as List).add(articleReactionDto)
        }
        articleReactionsDtoList = null

        //fetch all comment users have made from userId list
        def articleCommentDtoList = adminArticleService.listAllCommentsByUserIds(userIds)
        articleCommentDtoList.each { articleCommentDto ->
            def userId = articleCommentDto.userDto.id as Long
            articleCommentDto.userDto = null //don't need this
            articleCommentDto.commentReactions = null //neither do this
            (exportData[userId]['comments'] as List).add(articleCommentDto)
        }
        articleCommentDtoList = null //garbage collect please

        //fetch all comment reactions users have made
        def articleCommentReactionDtoList = adminArticleService.listAllArticleCommentReactionsByUserIds(userIds)
        articleCommentReactionDtoList.each { articleCommentReactionDto ->
            def userId = articleCommentReactionDto.userDto.id as Long
            articleCommentReactionDto.userDto = null
            (exportData[userId]['commentReactions'] as List).add(articleCommentReactionDto)
        }
        articleCommentReactionDtoList = null


        //fetch all user connection requests users have made (not the ones that made TO them)
        def connectionRequestsDtoList = adminUserConnectionService.findAllRequestsMadeFromUserIds(userIds)
        connectionRequestsDtoList.each {userConnectionRequestDto ->
            def userId = userConnectionRequestDto.userId as Long
            (exportData[userId]['connectionRequests'] as List).add(userConnectionRequestDto)
        }
        connectionRequestsDtoList = null

        //fetch all user connections of the users
        def connectionDtoList = adminUserConnectionService.findAllByUserIds(userIds)
        connectionDtoList.each {userConnectionDto ->
            def userId1 = userConnectionDto.userId as Long
            def userId2 = userConnectionDto.connectedUserId as Long
            if(userIds.contains(userId1)){
                (exportData[userId1]['connections'] as List).add(userConnectionDto)
            }

            if(userIds.contains(userId2)){
                (exportData[userId2]['connections'] as List).add(userConnectionDto)
            }
        }
        connectionDtoList = null


        //fetch all chat related data for user
        def chatExportedData = adminChatRoomService.exportAllChatRoomDataForUserIds(userIds)
        chatExportedData.each{chatRoomData ->
            def userId1 = chatRoomData['relatedUser1'] as Long
            def userId2 = chatRoomData['relatedUser2'] as Long

            if(userIds.contains(userId1)){
                (exportData[userId1]['chats'] as List).add(chatRoomData)
            }

            if(userIds.contains(userId2)){
                (exportData[userId2]['chats'] as List).add(chatRoomData)
            }
        }
        chatExportedData = null
        return exportData
    }

}
