package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.admins.AdminRequestDto
import com.tedi.growthin.backend.dtos.admins.AdminRequestListDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.dtos.users.UserListDto
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
//        log.info("Successfully set restriction to '${lock}' for user with id ${user.id}")
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

//    TODO: here
    def updateUserAdminRequest(AdminRequestDto adminRequestDto, Authentication authentication) throws Exception {

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInAdminId = JwtService.extractAppUserId(userJwtToken)


    }
}
