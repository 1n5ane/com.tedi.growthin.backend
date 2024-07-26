package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.users.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserConnectionRequestDto
import com.tedi.growthin.backend.services.UserConnectionIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/connect")
@Slf4j
class UserConnectionController {

    @Autowired
    JwtService jwtService

    @Autowired
    UserConnectionIntegrationService userConnectionIntegrationService

    @PutMapping(value = "/{connectionRequestId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    //accept or decline connection request
    def updateConnectionRequest(@PathVariable("connectionRequestId") String connectionRequestId,
                                @RequestBody UserConnectionRequestDto userConnectionRequestDto,
                                Authentication authentication) {
        def response = [
                "success"              : true,
                "userConnectionRequest": null,
                "error"                : ""
        ]

        try {
            connectionRequestId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid connection request id '${connectionRequestId}'."
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        userConnectionRequestDto.id = connectionRequestId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userConnectionRequest = userConnectionIntegrationService.updateConnectionRequest(userConnectionRequestDto, authentication)
            userConnectionRequest.id = userConnectionRequestDto.id
            response['userConnectionRequest'] = userConnectionRequest
        } catch (ValidationException validationException) {
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (ForbiddenException forbiddenException) {
            response["success"] = false
            response["error"] = forbiddenException.getMessage()
            log.error("${userIdentifier} Failed to update connection request '${userConnectionRequestDto.id}': ${forbiddenException.getMessage()}")
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to update userConnectionRequest with id ${userConnectionRequestDto.id}: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = ['/user/{connectedUserId}'], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    // new follow/connect
    def createUserConnectionRequest(@PathVariable("connectedUserId") String connectedUserId,
                                    Authentication authentication) {
        def response = [
                "success"              : true,
                "userConnectionRequest": null,
                "error"                : ""
        ]

        try {
            connectedUserId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid connection userId"
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        UserConnectionRequestDto userConnectionRequestDto = new UserConnectionRequestDto()
        userConnectionRequestDto.connectedUserId = connectedUserId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def createdUserConnectionRequestDto = userConnectionIntegrationService.createConnectionRequest(userConnectionRequestDto, authentication)
            response["userConnectionRequest"] = createdUserConnectionRequestDto
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to create user connection request: "+validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} failed to create user connection request ${userConnectionRequestDto} : ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }


    @DeleteMapping(value = ['/user/{connectedUserId}'], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    // unfollow / unconnect
    def deleteUserConnection(@PathVariable("connectedUserId") String connectedUserId,
                             Authentication authentication) {
        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            connectedUserId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid user id '${connectedUserId}'."
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        //id and user id will be dynamicaly set
        UserConnectionDto userConnectionDto = new UserConnectionDto(
                null,
                null,
                connectedUserId.toLong()
        )

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"


        try {
            def res = userConnectionIntegrationService.deleteUserConnection(userConnectionDto, authentication)
            response["success"] = res
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} "+validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to delete connection: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }
}
