package com.tedi.growthin.backend.controllers

import com.tedi.growthin.backend.dtos.UserDto
import com.tedi.growthin.backend.services.users.UserIntegrationService
import com.tedi.growthin.backend.utils.exception.UserEmailExistsException
import com.tedi.growthin.backend.utils.exception.UserUsernameExistsException
import com.tedi.growthin.backend.utils.exception.UserValidationException
import com.tedi.growthin.backend.utils.exception.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

//TODO: implement register flow, getUserDetails etc...(CRUD)
//      on user registration (user details will be held in resource server db (except password))
//      and then on auth server...

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
class UserController {

    @Autowired
    UserIntegrationService userIntegrationService


//    @GetMapping(value = "/user/{id}", produces = "application/json;charset=UTF-8")
//    @PreAuthorize("hasRole('ADMIN')")
//    @ResponseBody
//    def getUser(@PathVariable("id") String id, Authentication authentication) {
//        def response = ["success": true,
//                        "user"   : null,
//                        "error"  : ""]
//        return new ResponseEntity<>(response, HttpStatus.OK)
//    }

    @PutMapping(value = ["/{id}"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def updateUser(@PathVariable("id") String id, Authentication authentication){
//  TODO: make a service that extracts claims from jwt token
//      Users with role user can only update his own info (admin can do whatever he likes)

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
        }catch(UserUsernameExistsException usernameExistsException){
            error = usernameExistsException.getMessage()
        }catch(UserEmailExistsException emailExistsException){
            error = emailExistsException.getMessage()
        }catch(ValidationException validationException){
            error = validationException.getMessage()
        }catch(Exception exception){
            error = "An error occured! Please try again later"
            log.error(exception.getMessage())
        }

        if(!error.isEmpty()){
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

        def emailExists = null
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
