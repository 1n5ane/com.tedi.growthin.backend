package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserConnection
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.notifications.NotificationDto
import com.tedi.growthin.backend.dtos.notifications.NotificationTypeDto
import com.tedi.growthin.backend.repositories.users.UserConnectionRepository
import com.tedi.growthin.backend.repositories.users.UserConnectionRequestRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.notifications.NotificationService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionException
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionRequestException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime


@Service
@Slf4j
class UserConnectionService {

    @Autowired
    UserConnectionRequestRepository userConnectionRequestRepository

    @Autowired
    UserConnectionRepository userConnectionRepository

    @Autowired
    UserRepository userRepository

    //THIS BREAKS THE 'S' FROM SOLID PRINCIPLE (SINGLE RESPONSIBILITY)
    //The notification logic shouldn't be here
    //It should be called from the caller of this service
    //THIS IS DONE FOR THE SAKE OF ASSIGNMENT (no time for setting up a robust notification system with decoupled architecture...)
    //BECAUSE WE WANT NOTIFICATION TO BE SENT IN THE SAME TRANSACTION
    // so in case of app restart or in case of failed notification -> also rollback action (comment, commentReaction, articleReaction) so that client can retry....
    //If this was meant for production we would need a whole different architecture for event handling (Redis, activeMq etc...)
    //So when the caller gets succes when publishing a message -> the message would be 100% delivered [at least once (if not exactly once )] <- that's a whole different story
    @Autowired
    NotificationService notificationService

    List<Long> getConnectedUserIdsByUserId(Long userId) throws Exception {
        return userConnectionRepository.findAllConnectedUserIdsByUserId(userId)
    }


    Page<UserConnectionRequest> listAllUserConnectionRequestsByStatus(String requestType,
                                                                      Long userId,
                                                                      UserConnectionRequestStatus status,
                                                                      Integer page,
                                                                      Integer pageSize,
                                                                      String sortBy,
                                                                      String order) throws Exception {


        if (!["incoming", "outgoing"].contains(requestType)) {
            throw new UserConnectionRequestException("requestType can either be incoming or outgoing")
        }

        //requests made by the user with userId
        //check if userId exists
        def optionalUser = userRepository.findById(userId)

        if (optionalUser.isEmpty()) {
            throw new UserConnectionRequestException("User reference id '${userId}' not found")
        }

        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<UserConnectionRequest> pageUserConnectionRequest

        if (requestType == "incoming") {
            pageUserConnectionRequest = userConnectionRequestRepository.findAllToUserByUserIdAndStatus(userId, status.name(), pageable)
        } else {
            pageUserConnectionRequest = userConnectionRequestRepository.findAllFromUserByUserIdAndStatus(userId, status.name(), pageable)
        }

        return pageUserConnectionRequest
    }

    Page<UserConnection> listAllUserConnections(Long userId, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        //check if userId exists
        def optionalUser = userRepository.findById(userId)

        if (optionalUser.isEmpty()) {
            throw new UserConnectionException("User reference id '${userId}' not found")
        }

        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<UserConnection> pageUserConnection = userConnectionRepository.findAllByUserId(userId, pageable)

        return pageUserConnection
    }

    @Transactional(rollbackFor = Exception.class)
    def createUserConnectionRequest(UserConnectionRequestDto userConnectionRequestDto, notify = true) throws Exception {
        //first check if the user that currentLoggedInUser wants to connect with
        //already made a connection request
        //in that case currentLoggedInUser can make a new request to that ap[rticular user
        UserConnectionRequest userConnectionRequest = userConnectionRequestRepository.findByUserIdAndConnectedUserId(
                (Long) userConnectionRequestDto.connectedUserId,
                (Long) userConnectionRequestDto.userId
        )
        if (userConnectionRequest != null) {
            if (userConnectionRequest.status == UserConnectionRequestStatus.PENDING) {
                throw new UserConnectionRequestException(
                        "User has a PENDING request from user with id '${userConnectionRequestDto.connectedUserId}'"
                )
            } else if (userConnectionRequest.status == UserConnectionRequestStatus.ACCEPTED) {
                throw new UserConnectionRequestException("Users are already connnected")
            } else {
                //this is case where the other user made a request to currentLoggedInUser
                //but he declined
                //It seems that now he changed his mind
                //and now he requests to connect

                //swap user ids
                def tmpUser = userConnectionRequest.user
                userConnectionRequest.user = userConnectionRequest.connectedUser
                userConnectionRequest.connectedUser = tmpUser
                userConnectionRequest.status = UserConnectionRequestStatus.PENDING
                userConnectionRequest.updatedAt = OffsetDateTime.now()

                return userConnectionRequestRepository.save(userConnectionRequest)
            }
        }

        userConnectionRequest = userConnectionRequestRepository.findByUserIdAndConnectedUserId(
                (Long) userConnectionRequestDto.userId,
                (Long) userConnectionRequestDto.connectedUserId
        )

        if (!userConnectionRequest) {
            def user = new User()
            user.id = userConnectionRequestDto.userId

            //get connected user
            def optionalConnectedUser = userRepository.findById((Long) userConnectionRequestDto.connectedUserId)

            if (optionalConnectedUser.isEmpty()) {
                throw new UserConnectionRequestException("User reference id '${userConnectionRequestDto.connectedUserId}' not found")
            }

            //there is no connection request -> insert new
            userConnectionRequest = new UserConnectionRequest(
                    user,
                    optionalConnectedUser.get(),
                    UserConnectionRequestStatus.PENDING
            )
        } else if (userConnectionRequest.status == UserConnectionRequestStatus.PENDING) {
            //there is a pending request -> don't create new
            throw new UserConnectionRequestException("A pending connection request already exists for that user")
        } else if (userConnectionRequest.status == UserConnectionRequestStatus.ACCEPTED) {
            throw new UserConnectionRequestException("Users are already connnected")
        } else {
            //user deleted user in the past and now connects again -> so change status
            userConnectionRequest.status = UserConnectionRequestStatus.PENDING
            userConnectionRequest.updatedAt = OffsetDateTime.now()
        }

        userConnectionRequest = userConnectionRequestRepository.save(userConnectionRequest)

        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        //NOTIFY LOGIC...
        if (notify) {
            //notify connected user for the request
            def notificationDto = new NotificationDto(
                    null,
                    UserService.userDtoFromUser(userConnectionRequest.user),
                    UserService.userDtoFromUser(userConnectionRequest.connectedUser),
                    new NotificationTypeDto(5, "CONNECTION_REQUEST"),
                    true,
                    new UserConnectionRequestDto(
                            userConnectionRequest.id,
                            userConnectionRequest.user.id,
                            userConnectionRequest.connectedUser.id,
                            userConnectionRequest.status,
                            userConnectionRequest.createdAt,
                            userConnectionRequest.updatedAt
                    ),
                    false
            )
            notificationService.createNotification(notificationDto)
            log.info("Successfully created notification for user connection request")
        }

        return userConnectionRequest
    }

    //nested transactional annotations will be in single transaction (spring's default behavior)
    @Transactional(rollbackFor = Exception.class)
    UserConnection updateUserConnectionRequestAndCreateUserConnection(UserConnectionRequestDto userConnectionRequestDto, Boolean notify = true) throws Exception {
        def connectionRequest = this.updateUserConnectionRequest(userConnectionRequestDto)
        def userConnection = new UserConnectionDto(
                null,//will be dynamicaly generated by sequence
                connectionRequest.user.id,
                userConnectionRequestDto.connectedUserId
        )
        userConnection = this.createUserConnection(userConnection)

        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        //Notify user that connection request was accepted
        if (notify) {
            //User that accepted request (connectedUser) notifies user (who made the request) that accepted it
            def notificationDto = new NotificationDto(
                    null,
                    UserService.userDtoFromUser(connectionRequest.connectedUser),
                    UserService.userDtoFromUser(connectionRequest.user),
                    new NotificationTypeDto(5, "CONNECTION_REQUEST"),
                    true,
                    new UserConnectionRequestDto(
                            connectionRequest.id,
                            connectionRequest.user.id,
                            connectionRequest.connectedUser.id,
                            connectionRequest.status,
                            connectionRequest.createdAt,
                            connectionRequest.updatedAt
                    ),
                    false
            )
            notificationService.createNotification(notificationDto)
            log.info("Successfully created notification for accepting user connection request")
        }

        return userConnection
    }

    //only update status of pending user connection request to ACCEPTED/DECLINED
    @Transactional(rollbackFor = Exception.class)
    def updateUserConnectionRequest(UserConnectionRequestDto userConnectionRequestDto) throws Exception {
        Optional<UserConnectionRequest> optionalUserConnectionRequest = userConnectionRequestRepository.findById(
                (Long) userConnectionRequestDto.id
        )

        if (optionalUserConnectionRequest.isEmpty()) {
            throw new UserConnectionRequestException("User connection with id '${userConnectionRequestDto.id}' not found")
        }

        def connectionRequest = optionalUserConnectionRequest.get()

        if (connectionRequest.connectedUser.id != userConnectionRequestDto.connectedUserId) {
            throw new ForbiddenException("User can only ACCEPT/DECLINE his own connection requests")
        }

        //if already accepted -> user can't modify connection request
        if (connectionRequest.status != UserConnectionRequestStatus.PENDING) {
            throw new UserConnectionRequestException(
                    "User connection request is already ${connectionRequest.status}. Can't modify the status"
            )
        }

        connectionRequest.status = userConnectionRequestDto.status
        connectionRequest.updatedAt = OffsetDateTime.now()

        connectionRequest = userConnectionRequestRepository.save(connectionRequest)

        return connectionRequest
    }

    @Transactional(rollbackFor = Exception.class)
    def createUserConnection(UserConnectionDto userConnectionDto) throws Exception {
        if (userConnectionDto.userId == userConnectionDto.connectedUserId) {
            throw new UserConnectionException("Users can't connect with themselves")
        }

        //check if users are already connected
        Boolean usersAlreadyConnected = userConnectionRepository.existsUserConnection(
                (Long) userConnectionDto.userId,
                (Long) userConnectionDto.connectedUserId
        )

        if (usersAlreadyConnected) {
            throw new UserConnectionException("Users are already connected")
        }

        //if not already connected -> connect them
        def user1 = new User()
        user1.id = userConnectionDto.userId

        def user2 = new User()
        user2.id = userConnectionDto.connectedUserId

        UserConnection userConnection = new UserConnection(
                null,
                user1,
                user2
        )

        userConnection = userConnectionRepository.save(userConnection)

        return userConnection
    }


    @Transactional(rollbackFor = Exception.class)
    def removeUserConnection(UserConnectionDto userConnectionDto) throws Exception {
        Optional<UserConnection> optionalUserConnection = userConnectionRepository.findByUserIds(
                (Long) userConnectionDto.userId,
                (Long) userConnectionDto.connectedUserId
        )

        if (optionalUserConnection.isEmpty()) {
            throw new UserConnectionException("Users are not connected [userId1 = '${userConnectionDto.userId}', 'userId2 = ${userConnectionDto.connectedUserId}']")
        }

        def userConnection = optionalUserConnection.get()

        //remove connection
        userConnectionRepository.deleteById(userConnection.id)


        //also remove userConnectionRequest (don't care who initiated the userConnectionRequest)
        Optional<UserConnectionRequest> optionalUserConnectionRequest = userConnectionRequestRepository.findByUserIds(
                (Long) userConnectionDto.userId,
                (Long) userConnectionDto.connectedUserId
        )

        if (optionalUserConnectionRequest.isPresent()) {
            def userConnectionRequest = optionalUserConnectionRequest.get()
            userConnectionRequestRepository.deleteById(userConnectionRequest.id)
        }

        return true

    }

    //get connected user ids from userIdsList for userId
    List<Long> getConnectedUserIdsFromIdList(Long userId, List<Long> userIdsList) throws Exception {
        return userConnectionRepository.findAllConnectedUserIdsByUserIdList(userId, userIdsList)
    }

    //get connected user ids from userUsernamesList for userId
    List<Long> getConnectedUserIdsFromUsernameList(Long userId, List<String> userUsernamesList) throws Exception {
        return userConnectionRepository.findAllConnectedUserIdsByUserUsernameList(userId, userUsernamesList)
    }

    def checkUserConnectionExists(UserConnectionDto userConnectionDto) throws Exception {

        //check if connectedUserId exists
        Optional<User> optionalUser = userRepository.findById((Long) userConnectionDto.connectedUserId)
        if (optionalUser.isEmpty()) {
            throw new UserConnectionException("User reference id '${userConnectionDto.connectedUserId}' not found")
        }

        Boolean usersAlreadyConnected = userConnectionRepository.existsUserConnection(
                (Long) userConnectionDto.userId,
                (Long) userConnectionDto.connectedUserId
        )

        return usersAlreadyConnected
    }
}
