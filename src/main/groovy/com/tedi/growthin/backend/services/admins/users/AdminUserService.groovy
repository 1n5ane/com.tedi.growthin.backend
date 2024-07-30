package com.tedi.growthin.backend.services.admins.users

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.services.users.UserService
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

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
}
