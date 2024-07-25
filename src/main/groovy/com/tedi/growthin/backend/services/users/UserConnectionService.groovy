package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.users.UserConnectionRequestDto
import com.tedi.growthin.backend.repositories.UserConnectionRequestRepository
import com.tedi.growthin.backend.repositories.UserRepository
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionRequestException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime

//TODO: update status logic

@Service
class UserConnectionService {

    @Autowired
    UserConnectionRequestRepository userConnectionRequestRepository

    @Autowired
    UserRepository userRepository

    @Transactional(rollbackFor = Exception.class)
    def createUserConnectionRequest(UserConnectionRequestDto userConnectionRequestDto) throws Exception {
        UserConnectionRequest userConnectionRequest = userConnectionRequestRepository.findByUserIdAndConnectedUserId(
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
            throw new UserConnectionRequestException("A pending connection request already exists")
        } else if (userConnectionRequest.status == UserConnectionRequestStatus.ACCEPTED) {
            throw new UserConnectionRequestException("Users are already connnected")
        } else {
            //user deleted user in the past and now connects again -> so change status
            userConnectionRequest.status = UserConnectionRequestStatus.PENDING
            userConnectionRequest.updatedAt = OffsetDateTime.now()
        }

        userConnectionRequest = userConnectionRequestRepository.save(userConnectionRequest)

        return userConnectionRequest
    }
}
