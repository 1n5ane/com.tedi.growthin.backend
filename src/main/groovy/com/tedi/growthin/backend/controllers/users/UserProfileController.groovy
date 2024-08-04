package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.userProfiles.UserProfileDto
import com.tedi.growthin.backend.services.UserProfileIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/profile")
@Slf4j
class UserProfileController {

    @Autowired
    UserProfileIntegrationService userProfileIntegrationService


    @PostMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def registerProfile(@RequestBody UserProfileDto userProfileDto, Authentication authentication) {
        def response = [
                "success": true,
                "profile": null,
                "error"  : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"
        try{
            def createdUserProfileDto = userProfileIntegrationService.registerUserProfile(userProfileDto, authentication)
            response["profile"] = createdUserProfileDto
        }catch(ValidationException validationException){
            log.trace("${userIdentifier} Failed to register user profile: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        }catch(Exception exception){
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to register user profile: ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PutMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def updateProfile(@RequestBody UserProfileDto userProfileDto, Authentication authentication) {
        def response = [
                "success": true,
                "profile": null,
                "error"  : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"
        try{
            def updatedUserProfileDto = userProfileIntegrationService.updateUserProfile(userProfileDto, authentication)
            response["profile"] = updatedUserProfileDto
        }catch(ValidationException validationException){
            log.trace("${userIdentifier} Failed to update user profile: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        }catch(Exception exception){
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to update user profile: ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    //findById
//    @PostMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
//    @PreAuthorize("hasAnyRole('USER','ADMIN')")
//    @ResponseBody

    //search By username like

    //listAll
}
