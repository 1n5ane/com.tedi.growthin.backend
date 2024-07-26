package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.repositories.users.UserAdminRequestRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.utils.DateTimeService
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime

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
                (userDto.phone && !userDto.phone.isEmpty()) ? userDto.phone : null,
                (userDto.area && !userDto.area.isEmpty()) ? userDto.area : null,
                (userDto.country && !userDto.country.isEmpty()) ? userDto.country : null
        )

        //create user to resource server
        def registeredUser = userRepository.save(user)

        //if createAdminRequest -> Insert new AdminRequest (In case of exception everything will be rolled back)
        if (createAdminRequest) {
            UserAdminRequest userAdminRequest = new UserAdminRequest(registeredUser)
            adminRequestRepository.save(userAdminRequest)
        }

        //if successfull -> then create user to auth server
        //in case auth server fails -> resource server creation will be rolled back
        //and error will be returned
        def authServerResponse = userAuthServerService.registerUser(userDto)
        if(!authServerResponse["success"]){
            throw new UserValidationException("${authServerResponse["error"]}")
        }
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

    def getUserById(Long userId) {
        // user must be authenticated and authorized (also get password from auth server)
        Optional<User> optionalUser = userRepository.findById(userId)
        UserDto userDto = null
        if (optionalUser.isPresent())
            userDto = userDtoFromUser(optionalUser.get())
        return userDto
    }

    def getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
        UserDto userDto = null
        if (user)
            userDto = userDtoFromUser(user)
        return userDto
    }

    private def static userDtoFromUser(User user) {
        new UserDto(
                user.id,
                user.username,
                user.firstName,
                user.lastName,
                user.email,
                user.isAdmin ? ['ROLE_USER', 'ROLE_ADMIN'] : ['ROLE_USER'],
                user.phone,
                user.country,
                user.area,
                user.createdAt,
                user.updatedAt
        )
    }

    def listAllUsers() {
        //list all app users (not auth server users)
    }

    // a jwt token is provided because auth server is updated too!
    @Transactional(rollbackFor = Exception.class)
    def updateUser(UserDto userDto, String jwtToken) {
        User user = new User(
                userDto.id as Long,
                userDto.username,
                userDto.email,
                userDto.name,
                userDto.surname,
                (userDto.phone && !userDto.phone.isEmpty()) ? userDto.phone : null,
                (userDto.area && !userDto.area.isEmpty()) ? userDto.area : null,
                (userDto.country && !userDto.country.isEmpty()) ? userDto.country : null,
                userDto.authorities.contains("ROLE_ADMIN"),
                null,
                OffsetDateTime.now()
        )
        //first update user to resource server
        user = userRepository.save(user)

        //if successfull -> then update user to auth server
        //in case auth server fails -> resource server update will be rolled back
        //and error will be returned
        def authServerResponse = userAuthServerService.updateUser(userDto, jwtToken)
        if(!authServerResponse["success"]){
            throw new UserValidationException("${authServerResponse["error"]}")
        }
        return userDtoFromUser(user)
    }

}
