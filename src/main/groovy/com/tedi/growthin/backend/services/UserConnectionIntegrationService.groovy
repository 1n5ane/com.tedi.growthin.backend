package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.users.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserConnectionRequestDto
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionRequestException
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
    JwtService jwtService

    @Autowired
    UserConnectionService userConnectionService

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
                    "accepted connection request by user with id '${userConnection.user.id}'" +
                    "and a new connection was created with id '${userConnection.id}'")
            userIdMadeTheRequest = userConnection.user.id
        } else if (connectionRequestDto.status == UserConnectionRequestStatus.DECLINED) {
            def userConnectionRequest = userConnectionService.updateUserConnectionRequest(connectionRequestDto)
            log.info("User with id '${userConnectionRequest.connectedUser.id}' successfuly declined" +
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
    Boolean deleteUserConnection(UserConnectionDto userConnectionDto, Authentication authentication) throws Exception{
        //this method deletes entry from user_connections
        //also delete record from user_connection_requests

        def userJwtToken = (Jwt) authentication.getCredentials()

        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        userConnectionDto.userId = currentLoggedInUserId

        userConnectionService.removeUserConnection(userConnectionDto)

        return true

    }

}
