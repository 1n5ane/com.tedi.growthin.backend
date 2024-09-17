package com.tedi.growthin.backend.services.admins.users

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.admins.AdminRequestDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.admins.requests.AdminRequestException
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime

@Service
class AdminUserService extends UserService {

    def listAllRestrictedUsers(Integer page, Integer pageSize, String sortBy, String order, Boolean restricted) throws Exception {
        //list all app users (not auth server users)
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

//      list all restricted (or not) users
        Page<User> pageUser = this.userRepository.findAllByLocked(restricted, pageable)

        return pageUser

    }

    def listAllAdminUserRequestsByStatus(AdminRequestStatus status, Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        //list all app users (not auth server users)
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

//      list all restricted (or not) users
        Page<UserAdminRequest> pageUserAdminRequest = this.adminRequestRepository.findAllByStatus(status.name(), pageable)

        return pageUserAdminRequest

    }

    @Transactional(rollbackFor = Exception.class)
    def updateAdminUserRequest(AdminRequestDto adminRequestDto, String jwtToken) throws Exception {
        Optional<UserAdminRequest> optionalAdminRequest = adminRequestRepository.findById((Long) adminRequestDto.id)

        if (optionalAdminRequest.isEmpty()) {
            throw new AdminRequestException("Request with id '${adminRequestDto.id}' was not found")
        }

        def adminRequest = optionalAdminRequest.get()
        if (adminRequest.status == adminRequestDto.status) {
            //if status unchanged don't update
            return null
        }

        def updatedUserAuthorities

        if (adminRequestDto.status == AdminRequestStatus.ACCEPTED) {
            updatedUserAuthorities = ["ROLE_USER", "ROLE_ADMIN"]
        } else if (adminRequestDto.status == AdminRequestStatus.DECLINED || adminRequestDto.status == AdminRequestStatus.PENDING) {
            updatedUserAuthorities = ["ROLE_USER"]
        } else {
            throw new AdminRequestException("Unknown status '${adminRequestDto.status.name()}'")
        }

        adminRequest.status = adminRequestDto.status
        adminRequest.curatedByAdmin = new User()
        adminRequest.curatedByAdmin.id = adminRequestDto.curatedByAdminId
        adminRequest.updatedAt = OffsetDateTime.now()
        adminRequest = adminRequestRepository.save(adminRequest)

        //grant or revoke privileges
        adminRequest.user.isAdmin = updatedUserAuthorities.contains("ROLE_ADMIN")
        this.userRepository.save(adminRequest.user)

        //get users id in auth server and then update
        def authServerResponse = userAuthServerService.searchUserByUsername(adminRequest.user.username, jwtToken)

        if (!authServerResponse["success"]) {
            throw new AdminRequestException("${authServerResponse["error"]}")
        } else if (authServerResponse["user"] == null) {
            throw new AdminRequestException("User with username '${adminRequest.user.username}' not found on authorization server")
        }

        def authServerUserId = authServerResponse["user"]["id"] as Long

        def userDto = userDtoFromUser(adminRequest.user)
        userDto.id = authServerUserId

        //update roles in auth server too!
        def res = this.userAuthServerService.updateUser(userDto, jwtToken)
        if (!res["success"]) {
            throw new AdminRequestException("Failed to update user privileges in auth server for user with id '${adminRequest.user.id}' [requestId = ${adminRequest.id}]")
        }

        return new AdminRequestDto(
                adminRequest.id,
                adminRequest.user.id,
                adminRequest.curatedByAdmin.id,
                adminRequest.status,
                adminRequest.createdAt,
                adminRequest.updatedAt
        )
    }

    List<UserDto> findAllUsersByUserIds(List<Long> userIds) throws Exception {
        List<User> userList = userRepository.findAllByIds(userIds)
        List<UserDto> userDtoList = []

        userList.each{u->
            userDtoList.add(userDtoFromUser(u))
        }
        return userDtoList
    }
}
