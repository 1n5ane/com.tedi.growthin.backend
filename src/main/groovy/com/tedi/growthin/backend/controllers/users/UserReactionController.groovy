package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.services.UserReactionIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/reaction")
@Slf4j
class UserReactionController {

    @Autowired
    UserReactionIntegrationService userReactionIntegrationService

    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllReactions(Authentication authentication) {
        def response = [
                "success"  : true,
                "reactions": null,
                "error"    : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def reactions = userReactionIntegrationService.findAllReactions(authentication)
            response["reactions"] = reactions
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to get all reactions: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to get all reactions: ${exception.getMessage()}")
        }
        return new ResponseEntity<>(response, HttpStatus.OK)
    }
}
