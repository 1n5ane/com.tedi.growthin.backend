package com.tedi.growthin.backend.controllers.users


import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.UserIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
class UserController {

    @Autowired
    UserIntegrationService userIntegrationService


    //TODO: heeereeee for autocomplete + (make a seperate one that returns the whole user entities for searching)
    @GetMapping(value = "/search", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def searchUsersBySearchTerm() {
        //return list of user usernames + ids that contain the search term in their username
    }

    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    //list all users
    //on connected users also include email + phone
    def listAllUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
                     @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                     @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                     @RequestParam(name = "order", defaultValue = "desc") String order,
                     Authentication authentication) {

        def response = [
                "success"    : true,
                "hasNextPage": false,
                "users"      : [],
                "totalPages" : null,
                "error"      : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try{
            def userListDto = userIntegrationService.findAllUsers(page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userListDto.totalPages
            response["totalPages"] = userListDto.totalPages
            response["users"] = userListDto.users
        }catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        }catch(Exception exception){
            log.error("${userIdentifier} Failed to list all users: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = "/{username}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def getUser(@PathVariable("username") String username, Authentication authentication) {
        def response = ["success": true,
                        "user"   : null,
                        "error"  : ""]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        UserDto userDto = new UserDto()
        userDto.username = username
        try {
            def user = userIntegrationService.getUser(userDto, authentication)
            response["user"] = user
            if (!user) {
                response["success"] = false
                response["error"] = "User with username '${username}' was not found".toString()
                return new ResponseEntity<>(response, HttpStatus.OK)
            }
        } catch (ValidationException validationException) {
            log.error("${userIdentifier} Failed to get user '${username}': ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to get user '${username}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PutMapping(value = ["/{id}"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    //from this entry point user can only update his own info (both admin and user)
    //user details can be updated from admin controller (only by admins)
    def updateUser(@PathVariable("id") String id, @RequestBody UserDto user, Authentication authentication) {
        Long userId
        def response = [
                "success": true,
                "user"   : null,
                "error"  : ""
        ]
        try {
            userId = id.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid User Id"
            return new ResponseEntity<>(response, HttpStatus.OK)
        }
        user.id = userId
        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"
        try {
            def updatedUser = userIntegrationService.updateUser(user, authentication)
            response["user"] = updatedUser
        } catch (ForbiddenException forbiddenException) {
            response["success"] = false
            response["error"] = forbiddenException.getMessage()
            log.error("${userIdentifier} Failed to update user '${id}': ${forbiddenException.getMessage()}")
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            response["success"] = false
            response["error"] = validationException.getMessage()
            log.error("${userIdentifier} Failed to update user '${id}': ${validationException.getMessage()}")
        } catch (Exception exception) {
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to update user '${id}': ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @ResponseBody
    def registerUser(@RequestBody UserDto userDto) {
        def response = ["success": true,
                        "user"   : null,
                        "error"  : ""]

        def error = ""
        try {
            UserDto registeredUser = userIntegrationService.registerUser(userDto)
            response["user"] = registeredUser
        } catch (ValidationException validationException) {
            error = validationException.getMessage()
        } catch (Exception exception) {
            error = "An error occured! Please try again later"
            log.error(exception.getMessage())
        }

        if (!error.isEmpty()) {
            response["success"] = false
            response["error"] = error
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = "/exists", produces = "application/json;charset=UTF-8")
    @ResponseBody
    def checkUserExists(@RequestParam(required = false, name = "username") String username,
                        @RequestParam(required = false, name = "email") String email) {
        def response = ["success": true,
                        "exists" : false,
                        "error"  : ""]
        if (!username && !email) {
            response["success"] = false
            response["exists"] = null
            response["error"] = "No username or email provided!"
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        Boolean usernameExists = null
        def error = ""
        if (username && !username.isEmpty()) {
            try {
                usernameExists = userIntegrationService.checkUserExistsByUsername(username)
                response["exists"] = usernameExists
            } catch (UserValidationException userValidationException) {
                error = userValidationException.getMessage()
            } catch (Exception exception) {
                error = "An error occured."
                log.error(exception.getMessage())
            }
        }

        if (!error.isEmpty()) {
            response["success"] = false
            response["error"] = error
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def emailExists
        if (!usernameExists && email && !email.isEmpty()) {
            try {
                emailExists = userIntegrationService.checkUserExistsByEmail(email)
                response["exists"] = emailExists
            } catch (UserValidationException userValidationException) {
                error = userValidationException.getMessage()
            } catch (Exception exception) {
                error = "An error occured."
                log.error(exception.getMessage())
            }
        }

        if (!error.isEmpty()) {
            response["success"] = false
            response["error"] = error
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }
}
