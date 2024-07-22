package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.UserDto
import com.tedi.growthin.backend.services.validation.UserValidationService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.UserEmailExistsException
import com.tedi.growthin.backend.utils.exception.UserUsernameExistsException
import com.tedi.growthin.backend.utils.exception.UserValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserService userService

//   TODO: add admin_request table for admin requests on user register

    def registerUser(UserDto userDto) throws Exception{
        validationServiceMap['userValidationService'].validate(userDto)

        //All users initially registered with ROLE_USER
        //if user contains ROLE_ADMIN a request is made so that admins can approve or deny
        def createAdminRequest = false
        if( userDto.authorities?.contains("ROLE_ADMIN")){
            createAdminRequest = true
        }
        userDto.authorities = ["ROLE_USER"]

        //first check if username or email not exists (both on resource and auth server)
        def usernameExists = checkUserExistsByUsername(userDto.username)
        if(usernameExists){
            throw new UserUsernameExistsException("User with username '${userDto.username}' already exists")
        }


        def emailExists = checkUserExistsByEmail(userDto.email)
        if(emailExists){
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
                user.area
        )

    }

    def checkUserExistsByEmail(String email) throws Exception{
        //check if user exists in both auth server and resource one
        def isValid = UserValidationService.emailIsValid(email)
        if(!isValid)
            throw new UserValidationException("Email is invalid")

        return userService.checkUserExistsByEmail(email)

    }

    def checkUserExistsByUsername(String username) throws Exception{
        //check if user exists in both auth server and resource one
        def isValid = UserValidationService.usernameIsValid(username)
        if(!isValid)
            throw new UserValidationException("Username is invalid")

        return userService.checkUserExistsByUsername(username)
    }

}
