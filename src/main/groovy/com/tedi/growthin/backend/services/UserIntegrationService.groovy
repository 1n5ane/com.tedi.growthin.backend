package com.tedi.growthin.backend.services


import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.dtos.users.UserListDto
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.users.UserValidationService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import com.tedi.growthin.backend.utils.exception.validation.users.UserEmailExistsException
import com.tedi.growthin.backend.utils.exception.validation.users.UserUsernameExistsException
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserService userService

    @Autowired
    UserConnectionService userConnectionService

    UserListDto findAllUsers(Integer page, Integer pageSize, String sortBy, String order, Authentication authentication) throws Exception {

        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "username", "firstName", "lastName", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, username, firstName, lastName, createdAt, updatedAt]")

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //get UserPage from service
        Page<User> pageUser = userService.listAllUsers(page, pageSize, sortBy, order)

        UserListDto userListDto = new UserListDto()
        userListDto.totalPages = pageUser.totalPages

        if (pageUser.isEmpty()) {
            return userListDto
        }

        //get which users are connected with user (list of ids)
        //and for the rest unconnected users -> remove phone and email
        //phone and email are private -> details that only connected users can view

        def connectedUserIds = userConnectionService.getConnectedUserIdsByUserId(currentLoggedInUserId)

        List<User> userList = pageUser.getContent()

        userList.each { u ->
            def isConnected = connectedUserIds.contains(u.id)
            if ((!isConnected) && (u.id != currentLoggedInUserId)) {
                //if not connected
                if (!u.isEmailPublic)
                    u.email = null

                if (!u.isPhonePublic)
                    u.phone = null

                if (!u.isAreaPublic)
                    u.area = null

                if (!u.isCountryPublic)
                    u.country = null
            }
            userListDto.users.add(UserService.userDtoFromUser(u))
        }

        return userListDto
    }

    UserDto getUser(UserDto userDto, Authentication authentication) throws Exception {
        UserDto user
        if (userDto.id != null) {
            user = userService.getUserById((Long) userDto.id)
        } else if (userDto.email != null && !userDto.email.isEmpty()) {
            user = userService.getUserByEmail(userDto.email)
        } else if (userDto.username != null && !userDto.username.isEmpty()) {
            user = userService.getUserByUsername(userDto.username)
        } else {
            throw new UserValidationException("Please provide user id, email or username")
        }

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //CHECK IF CURRENTLOGGEDIN USER EQUALS WITH FETCHED USER ID
        if (user != null && currentLoggedInUserId != (user.id as Long)) {
            //CHECK IF USERS ARE CONNECTED
            Boolean usersConnected = userConnectionService.checkUserConnectionExists(
                    new UserConnectionDto(null, currentLoggedInUserId, user.id)
            )
            //TODO: check if fields are public and return in that case
            if (!usersConnected) {
                //if users are not connected don't return email and phone
                if (!user.isEmailPublic)
                    user.email = null

                if (!user.isPhonePublic)
                    user.phone = null

                if (!user.isAreaPublic)
                    user.area = null

                if (!user.isCountryPublic)
                    user.country = null
            }
        }

        return user
    }

    UserDto registerUser(UserDto userDto) throws Exception {
        validationServiceMap['userValidationService'].validate(userDto)

        //All users initially registered with ROLE_USER
        //if user contains ROLE_ADMIN a request is made so that admins can approve or deny
        def createAdminRequest = false
        if (userDto.authorities?.contains("ROLE_ADMIN")) {
            createAdminRequest = true
        }
        userDto.authorities = ["ROLE_USER"]

        //first check if username or email not exists (both on resource and auth server)
        def usernameExists = checkUserExistsByUsername(userDto.username)
        if (usernameExists) {
            throw new UserUsernameExistsException("User with username '${userDto.username}' already exists")
        }


        def emailExists = checkUserExistsByEmail(userDto.email)
        if (emailExists) {
            throw new UserEmailExistsException("User with email '${userDto.email}' already exists")
        }

        User user = userService.registerUser(userDto, createAdminRequest)
        log.info("User '${user}' was successfully registered")
//        return created user info

        return new UserDto(
                user.id,
                user.username,
                user.firstName,
                user.lastName,
                user.email,
                ["ROLE_USER"],
                user.phone,
                user.country,
                user.area,
                user.isEmailPublic,
                user.isPhonePublic,
                user.isCountryPublic,
                user.isAreaPublic,
                user.createdAt
        )

    }

    UserDto updateUser(UserDto user, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()

        //get user by appId contained in token
        //users and admins can only update their own details
        //admins can update users' details from admin controller
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check if requested id to update match with his id or else return forbidden
        if (currentLoggedInUserId != user.id)
            throw new ForbiddenException("Users can only update their own details")

        if (!JwtService.extractAuthorities(userJwtToken).contains('ROLE_ADMIN')) {
            //if user has role user -> can't update authorities
            user.authorities = ['ROLE_USER']
        } else {
            user.authorities = ['ROLE_USER', 'ROLE_ADMIN']
        }

        user.locked = null

        //validate input
        validationServiceMap['userValidationService'].validate(user)

        //check if username/email updated and search for
        String currentLoggedInUsername = JwtService.extractUsername(userJwtToken)
        if (currentLoggedInUsername != user.username) {
            //user changed username
            if (checkUserExistsByUsername(user.username))
                throw new UserUsernameExistsException("Can't update username '${currentLoggedInUsername}'." +
                        " '${user.username}' is already associated with another account")
        }


        String currentLoggedInEmail = JwtService.extractEmail(userJwtToken)
        if (currentLoggedInEmail != user.email) {
            if (checkUserExistsByEmail(user.email))
                throw new UserEmailExistsException("Can't update email '${currentLoggedInEmail}'." +
                        " '${user.email}' is already associated with another account")
        }

        //all good so far -> continue with update
        def updatedUser = userService.updateUser(user, userJwtToken.getTokenValue())
        //log info to indicate success
        log.info("User with id '${updatedUser.id}' was successfully updated: ${updatedUser}")
        return updatedUser
    }

    Boolean checkUserExistsByEmail(String email) throws Exception {
        //check if user exists in both auth server and resource one
        def isValid = UserValidationService.emailIsValid(email)
        if (!isValid)
            throw new UserValidationException("Email is invalid")

        return userService.checkUserExistsByEmail(email)

    }

    Boolean checkUserExistsByUsername(String username) throws Exception {
        //check if user exists in both auth server and resource one
        def isValid = UserValidationService.usernameIsValid(username)
        if (!isValid)
            throw new UserValidationException("Username is invalid")

        return userService.checkUserExistsByUsername(username)
    }

}
