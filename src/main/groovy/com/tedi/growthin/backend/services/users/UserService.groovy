package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserAdminRequest
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.repositories.users.UserAdminRequestRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.utils.DateTimeService
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
                (userDto.country && !userDto.country.isEmpty()) ? userDto.country : null,
                userDto.isEmailPublic == null ? false : userDto.isEmailPublic,
                userDto.isPhonePublic == null ? false : userDto.isPhonePublic,
                userDto.isAreaPublic == null ? false : userDto.isAreaPublic,
                userDto.isCountryPublic == null ? false : userDto.isCountryPublic,
                false
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
        if (!authServerResponse["success"]) {
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

    def getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
        UserDto userDto = null
        if (user)
            userDto = userDtoFromUser(user)
        return userDto
    }

    def getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
        UserDto userDto = null
        if (user)
            userDto = userDtoFromUser(user)
        return userDto
    }

    def static userDtoFromUser(User user) {
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
                user.locked,
                user.createdAt,
                user.updatedAt,
                user.isPhonePublic,
                user.isEmailPublic,
                user.isCountryPublic,
                user.isAreaPublic
        )
    }

    def static userDtoFromUserWithHiddenPrivateFields(User user) {
        new UserDto(
                user.id,
                user.username,
                user.firstName,
                user.lastName,
                user.isEmailPublic ? user.email : null,
                user.isAdmin ? ['ROLE_USER', 'ROLE_ADMIN'] : ['ROLE_USER'],
                user.isPhonePublic ? user.phone : null,
                user.isCountryPublic ? user.country : null,
                user.isAreaPublic ? user.area : null,
                user.locked,
                user.createdAt,
                user.updatedAt,
                user.isPhonePublic,
                user.isEmailPublic,
                user.isCountryPublic,
                user.isAreaPublic
        )
    }

    def listAllUsers(Integer page, Integer pageSize, String sortBy, String order) throws Exception {
        //list all app users (not auth server users)

        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        Page<User> pageUser = userRepository.findAll(pageable)

        return pageUser
    }

    // a jwt token is provided because auth server is updated too!
    @Transactional(rollbackFor = Exception.class)
    def updateUser(UserDto userDto, String jwtToken) throws Exception {
        //first fetch current user (before update)
        Optional<User> optionalPreUpdatedUser = userRepository.findById((Long) userDto.id)

        if (optionalPreUpdatedUser.isEmpty())
            throw new UserValidationException("User with id '${userDto.id}' was not found")

        def preUpdatedUser = optionalPreUpdatedUser.get()

        //need to search to auth server using old username to get
        //auth server id
        def oldUsername = preUpdatedUser.username

        def updated = false

        if ((userDto.username != null && !userDto.username.isEmpty()) && (preUpdatedUser.username != userDto.username)) {
            preUpdatedUser.username = userDto.username
            updated = true
        }

        if ((userDto.email != null && !userDto.email.isEmpty()) && (preUpdatedUser.email != userDto.email)) {
            preUpdatedUser.email = userDto.email
            updated = true
        }

        if (userDto.isEmailPublic != null && preUpdatedUser.isEmailPublic != userDto.isEmailPublic) {
            preUpdatedUser.isEmailPublic = userDto.isEmailPublic
            updated = true
        }

        if ((userDto.name != null && !userDto.name.isEmpty()) && (preUpdatedUser.firstName != userDto.name)) {
            preUpdatedUser.firstName = userDto.name
            updated = true
        }

        if ((userDto.surname != null && !userDto.surname.isEmpty()) && (preUpdatedUser.lastName != userDto.surname)) {
            preUpdatedUser.username = userDto.username
            updated = true
        }

        if (userDto.phone != null && (preUpdatedUser.phone != userDto.phone)) {
            preUpdatedUser.phone = userDto.phone
            updated = true
        }

        if (userDto.isPhonePublic != null && preUpdatedUser.isPhonePublic != userDto.isPhonePublic) {
            preUpdatedUser.isPhonePublic = userDto.isPhonePublic
            updated = true
        }

        if ((userDto.area != null) && (preUpdatedUser.area != userDto.area)) {
            preUpdatedUser.area = userDto.area
            updated = true
        }

        if (userDto.isAreaPublic != null && preUpdatedUser.isAreaPublic != userDto.isAreaPublic) {
            preUpdatedUser.isAreaPublic = userDto.isAreaPublic
            updated = true
        }

        if ((userDto.country != null) && (preUpdatedUser.country != userDto.country)) {
            preUpdatedUser.country = userDto.country
            updated = true
        }

        if (userDto.isCountryPublic != null && preUpdatedUser.isCountryPublic != userDto.isCountryPublic) {
            preUpdatedUser.isCountryPublic = userDto.isCountryPublic
            updated = true
        }

        if ((userDto.authorities.contains("ROLE_ADMIN") && !preUpdatedUser.isAdmin) || (!userDto.authorities.contains("ROLE_ADMIN") && preUpdatedUser.isAdmin)) {
            preUpdatedUser.isAdmin = userDto.authorities.contains("ROLE_ADMIN")
            updated = true
        }

        if (userDto.locked != null) {
            preUpdatedUser.locked = userDto.locked
            updated = true
        } else {
            userDto.locked = preUpdatedUser.locked
        }

        if (updated) {
            preUpdatedUser.updatedAt = OffsetDateTime.now()
        }

        //first update user to resource server
        def updatedUser
        if (updated)
            updatedUser = userRepository.save(preUpdatedUser)
        else
            updatedUser = preUpdatedUser

        //fetch user by old username and get his authserver id to update

        def authServerResponse = userAuthServerService.searchUserByUsername(oldUsername, jwtToken)

        if (!authServerResponse["success"]) {
            throw new UserValidationException("${authServerResponse["error"]}")
        } else if (authServerResponse["user"] == null) {
            throw new UserValidationException("User with username '${oldUsername}' not found on authorization server")
        }

        def authServerUserId = authServerResponse["user"]["id"] as Long

        userDto.id = authServerUserId

        //if successfull -> then update user to auth server
        //in case auth server fails -> resource server update will be rolled back
        //and error will be returned
        authServerResponse = userAuthServerService.updateUser(userDto, jwtToken)
        if (!authServerResponse["success"]) {
            throw new UserValidationException("${authServerResponse["error"]}")
        }
        return userDtoFromUser(updatedUser)
    }

}
