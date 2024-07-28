package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import com.tedi.growthin.backend.domains.users.UserConnection
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.users.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserConnectionListDto
import com.tedi.growthin.backend.dtos.users.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.users.UserConnectionRequestListDto

import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionRequestException
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserConnectionIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserConnectionService userConnectionService

    @Autowired
    UserService userService


    //User connection requests made TO or BY The user
    UserConnectionRequestListDto findAllUserConnectionRequestsByStatus(String requestType,
                                                                       UserConnectionRequestStatus status,
                                                                       Integer page,
                                                                       Integer pageSize,
                                                                       String sortBy,
                                                                       String order,
                                                                       Authentication authentication) throws Exception {

        if(!["incoming","outgoing"].contains(requestType)){
            throw new UserConnectionRequestException("requestType can either be incoming or outgoing")
        }

        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order.trim()
        ])

        //validate sortBy here
        sortBy = sortBy.trim()
        if (!["id", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt, updatedAt]")

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)


        def userConnectionRequestsPage = userConnectionService.listAllUserConnectionRequestsByStatus(
                requestType,
                currentLoggedInUserId,
                status,
                page,
                pageSize,
                sortBy,
                order.trim()
        )

        UserConnectionRequestListDto userConnectionRequestListDto = new UserConnectionRequestListDto()

        def userDto = userService.getUserById(currentLoggedInUserId)
        userConnectionRequestListDto.user = userDto

        userConnectionRequestListDto.totalPages = userConnectionRequestsPage.totalPages

        if (userConnectionRequestsPage.isEmpty()) {
            return userConnectionRequestListDto
        }

        //fill user connection requests

        List<UserConnectionRequest> userConnectionRequestList = userConnectionRequestsPage.getContent()

        userConnectionRequestList.each { ucr ->
            userConnectionRequestListDto.requests.add([
                    "requestId": ucr.id,
                    "user"     : UserService.userDtoFromUser(ucr.user),
                    "createdAt": ucr.createdAt,
                    "updatedAt": ucr.updatedAt
            ])
        }

        return userConnectionRequestListDto
    }

    UserConnectionListDto findAllUserConnections(Long userId, Integer page, Integer pageSize, String sortBy, String order, Authentication authentication) throws Exception {

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

        //check if current logged in user request his own connections
        if (currentLoggedInUserId != userId) {
            //check if connected -> if not throw Forbidden exception (unconnected users can't view other users connections)
            def userConnectionDto = new UserConnectionDto(null, null, userId)
            if (!checkUserConnectionExists(userConnectionDto, authentication)) {
                throw new ForbiddenException("You are not connected with user with id ${userId}")
            }
        }

        //get connections for userId
        def userConnectionsPage = userConnectionService.listAllUserConnections(userId, page, pageSize, sortBy, order)

        UserConnectionListDto userConnectionListDto = new UserConnectionListDto()

        def userDto = userService.getUserById(userId)
        userConnectionListDto.user = userDto

        userConnectionListDto.totalPages = userConnectionsPage.totalPages

        if (userConnectionsPage.isEmpty()) {
            return userConnectionListDto
        }

        //fill user connections
        List<UserConnection> userConnectionList = userConnectionsPage.getContent()

        userConnectionList.each { uc ->
            userConnectionListDto.userConnections.add([
                    "userConnectionId": uc.id,
                    "user"            : userId != uc.user.id ? UserService.userDtoFromUser(uc.user) : UserService.userDtoFromUser(uc.connectedUser),
                    "createdAt"       : uc.createdAt
            ])
        }

        return userConnectionListDto
    }

    UserConnectionRequestDto createConnectionRequest(UserConnectionRequestDto connectionRequestDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()

        //get users app id (not userId as it's id in auth server) contained in token
        //users and admins can only update their own details
        //admins can only ban users not alter their details (altering details by admin sounds illegal)
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        connectionRequestDto.userId = currentLoggedInUserId

        //remove id if provided in request
        connectionRequestDto.id = null

        validationServiceMap['userConnectionRequestValidationService'].validate(connectionRequestDto)

        UserConnectionRequest userConnectionRequest = userConnectionService.createUserConnectionRequest(connectionRequestDto)

        log.info("User '${userConnectionRequest.user.id}' " +
                "succesfully made a connection request to user '${userConnectionRequest.connectedUser.id}'")

        return new UserConnectionRequestDto(
                userConnectionRequest.id,
                connectionRequestDto.userId,
                connectionRequestDto.connectedUserId,
                UserConnectionRequestStatus.PENDING,
                userConnectionRequest.createdAt,
                userConnectionRequest.updatedAt
        )
    }

    //if connection request is accepted -> a new entry is created in user_connections table
    UserConnectionRequestDto updateConnectionRequest(UserConnectionRequestDto connectionRequestDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //requests made to user can only accepted/declined by him
        connectionRequestDto.connectedUserId = currentLoggedInUserId
        def userIdMadeTheRequest
        if (connectionRequestDto.status == UserConnectionRequestStatus.ACCEPTED) {
            //this is executed in a single transaction
            //if either udpate or user connection creation fails -> rollback
            def userConnection = userConnectionService.updateUserConnectionRequestAndCreateUserConnection(connectionRequestDto)
            log.info("User with id '${userConnection.connectedUser.id}' " +
                    "accepted connection request by user with id '${userConnection.user.id}' " +
                    "and a new connection was created with id '${userConnection.id}'")
            userIdMadeTheRequest = userConnection.user.id
        } else if (connectionRequestDto.status == UserConnectionRequestStatus.DECLINED) {
            def userConnectionRequest = userConnectionService.updateUserConnectionRequest(connectionRequestDto)
            log.info("User with id '${userConnectionRequest.connectedUser.id}' successfuly declined " +
                    "connection request with id '${userConnectionRequest.id}' " +
                    "made by user with id '${userConnectionRequest.user.id}'")
            userIdMadeTheRequest = userConnectionRequest.user.id
        } else {
            throw new UserConnectionRequestException("status can either be 'ACCEPTED' or 'DECLINED'")
        }

        return new UserConnectionRequestDto(
                null,
                userIdMadeTheRequest,
                currentLoggedInUserId,
                connectionRequestDto.status
        )
    }

    // unfollow/unconnect
    // true indicate success
    Boolean deleteUserConnection(UserConnectionDto userConnectionDto, Authentication authentication) throws Exception {
        //this method deletes entry from user_connections
        //also delete record from user_connection_requests

        def userJwtToken = (Jwt) authentication.getCredentials()

        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        userConnectionDto.userId = currentLoggedInUserId

        userConnectionService.removeUserConnection(userConnectionDto)

        log.info("User with id '${currentLoggedInUserId}' successfully unconnected with user with id ${userConnectionDto.connectedUserId}")

        return true

    }

    Boolean checkUserConnectionExists(UserConnectionDto userConnectionDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        userConnectionDto.userId = currentLoggedInUserId

        def exists = userConnectionService.checkUserConnectionExists(userConnectionDto)
        return exists
    }

}
