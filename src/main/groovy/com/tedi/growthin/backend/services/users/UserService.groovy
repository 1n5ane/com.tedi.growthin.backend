package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.UserDto
import com.tedi.growthin.backend.repositories.UserAdminRequestRepository
import com.tedi.growthin.backend.repositories.UserRepository
import com.tedi.growthin.backend.services.utils.DateTimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService {

    @Autowired
    UserAuthServerService userAuthServerService

    @Autowired
    UserRepository userRepository

    @Autowired
    UserAdminRequestRepository adminRequestRepository

    @Autowired
    DateTimeService dateTimeService

    @Transactional(rollbackFor = Exception.class)
    def registerUser(UserDto userDto, Boolean createAdminRequest = false) throws Exception {
        User user = new User(
                userDto.username,
                userDto.email,
                userDto.name,
                userDto.surname,
                (userDto.phone && !userDto.phone.isEmpty())?userDto.phone:null,
                (userDto.area && !userDto.area.isEmpty())?userDto.area:null,
                (userDto.country && !userDto.country.isEmpty())?userDto.country:null
        )

        //create user to resource server
        def registeredUser = userRepository.save(user)

        //if createAdminRequest -> Insert new AdminRequest (In case of exception everything will be rolled back)
        if(createAdminRequest){
            UserAdminRequest userAdminRequest = new UserAdminRequest(registeredUser)
            adminRequestRepository.save(userAdminRequest)
        }

        //if successfull -> then create user to auth server
        //in case auth server fails -> resource server creation will be rolled back
        //and error will be returned
        userAuthServerService.registerUser(userDto)

        //return resource server registered user entity
        return registeredUser
    }

    def checkUserExistsByUsername(String username) throws Exception {
        //first check on resource server
        def resourceServerUserExists = userRepository.existsByUsername(username)
        if (resourceServerUserExists)
            return resourceServerUserExists

        // if not exists on resource server
        //then check on auth server
        def authServerUserExists = userAuthServerService.checkUserExistsByUsername(username)
        return authServerUserExists

    }

    def checkUserExistsByEmail(String email) throws Exception {
        //first check on resource server
        def resourceServerUserExists = userRepository.existsByEmail(email)
        if (resourceServerUserExists)
            return resourceServerUserExists

        // if not exists on resource server
        //then check on auth server
        def authServerUserExists = userAuthServerService.checkUserExistsByEmail(email)
        return authServerUserExists
    }

    def getUser(UserDto userDto) {
        // user must be authenticated and authorized (also get password from auth server)
        // check if id or username provided and get accordingly
    }

    def listAllUsers() {
        //list all app users (not auth server users)
    }

    @Transactional
    def updateUser(UserDto userDto) {
        //first update user to resource server

        //if successfull -> then update user to auth server
        //in case auth server fails -> resource server update will be rolled back
        //and error will be returned
    }

}
