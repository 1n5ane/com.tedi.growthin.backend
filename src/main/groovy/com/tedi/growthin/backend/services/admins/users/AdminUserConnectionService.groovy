package com.tedi.growthin.backend.services.admins.users

import com.tedi.growthin.backend.domains.users.UserConnection
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.services.users.UserConnectionService
import org.springframework.stereotype.Service

@Service
class AdminUserConnectionService extends UserConnectionService {

    List<UserConnectionRequestDto> findAllRequestsMadeFromUserIds(List<Long> userIds) throws Exception {
        List<UserConnectionRequest> userConnectionRequestList = this.userConnectionRequestRepository.findAllFromUserIds(userIds)
        List<UserConnectionRequestDto> userConnectionRequestDtoList = []
        userConnectionRequestList.each{ucr ->
            userConnectionRequestDtoList.add(new UserConnectionRequestDto(
                    ucr.id,
                    ucr.user.id,
                    ucr.connectedUser.id,
                    ucr.status,
                    ucr.createdAt,
                    ucr.updatedAt
            ))
        }
        return userConnectionRequestDtoList
    }

    List<UserConnectionDto> findAllByUserIds(List<Long> userIds) throws Exception {
        List<UserConnection> userConnectionList = this.userConnectionRepository.findAllByUserIds(userIds)
        List<UserConnectionDto> userConnectionDtoList = []
        userConnectionList.each {uc ->
            userConnectionDtoList.add(new UserConnectionDto(uc.id, uc.user.id, uc.connectedUser.id, uc.createdAt))
        }

        return userConnectionDtoList
    }
}
