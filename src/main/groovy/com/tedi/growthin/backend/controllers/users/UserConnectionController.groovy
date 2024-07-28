package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
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
@RequestMapping("/api/v1/connect")
@Slf4j
class UserConnectionController {

    @Autowired
    JwtService jwtService

    @Autowired
    UserConnectionIntegrationService userConnectionIntegrationService


    @GetMapping(value = "/user/{userId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllUserConnectionsByUserId(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                       @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                       @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                       @RequestParam(name = "order", defaultValue = "desc") String order,
                                       @PathVariable("userId") String userId,
                                       Authentication authentication) {
        def response = [
                "success"      : true,
                "hasNextPage"  : false,
                "user"         : null,
                "connectedWith": [],
                "totalPages"   : null,
                "error"        : ""
        ]

        try {
            userId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid user id '${userId}'.".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        //if users are connected or user requests his own connections return requested users' connections
        //else forbidden
        try {
            def userConnectionListDto = userConnectionIntegrationService.findAllUserConnections(userId.toLong(), page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userConnectionListDto.totalPages
            response["totalPages"] = userConnectionListDto.totalPages
            response["user"] = userConnectionListDto.user
            response["connectedWith"] = userConnectionListDto.userConnections
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = forbiddenException.getMessage()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list user connections for ${userId}: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }


    @GetMapping(value = "/requests/{requestType}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    //User connection requests made TO The user -> if type = incoming
    //User connection requests made BY The user -> if type = outgoing
    def listAllUserConnectionRequestsByStatus(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                              @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                              @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                              @RequestParam(name = "order", defaultValue = "desc") String order,
                                              @RequestParam(name = "status", defaultValue = "PENDING") String status,
                                              @PathVariable(name = "requestType") String requestType,
                                              Authentication authentication
    ) {
        def response

        if (!["incoming", "outgoing"].contains(requestType)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND)
        } else if (requestType == "incoming") {
            //list all incoming currentLoggedInUser requests by status
            response = [
                    "success"    : true,
                    "hasNextPage": false,
                    "status"     : null,
                    "requestedBy": [], //contains a list of maps -> ex. [["requestId":0, "user": UserDto, "createdAt":..., "updatedAt":...]]
                    "totalPages" : null,
                    "error"      : ""
            ]
        } else {
            //list all outgoing currentLoggedInUser requests by status
            response = [
                    "success"    : true,
                    "hasNextPage": false,
                    "status"     : null,
                    "requestedTo": [], //contains a list of maps -> ex. [["requestId":0, "user": UserDto, "createdAt":..., "updatedAt":...]]
                    "totalPages" : null,
                    "error"      : ""
            ]
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        def enumStatus
        try {
            enumStatus = Enum.valueOf(UserConnectionRequestStatus.class, status)
        } catch (IllegalArgumentException ignored) {
            log.trace("${userIdentifier} Invalid status '${status}' on list all ${requestType} connection requests.")
            response["success"] = false
            response["error"] = "Invalid status ${status} .Status can be PENDING, ACCEPTED or DECLINED".toString()
            //it's GSTRING
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        try {
            def userConnectionRequestListDto = userConnectionIntegrationService.findAllUserConnectionRequestsByStatus(
                    requestType,
                    enumStatus,
                    page,
                    pageSize,
                    sortBy,
                    order,
                    authentication
            )
            response["hasNextPage"] = (page + 1) < userConnectionRequestListDto.totalPages
            response["totalPages"] = userConnectionRequestListDto.totalPages
            response["status"] = enumStatus
            if(requestType == "incoming")
                response["requestedBy"] = userConnectionRequestListDto.requests
            else
                response["requestedTo"] = userConnectionRequestListDto.requests

        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list user connections requests of type '${requestType}' for ${JwtService.extractAppUserId(jwtToken)}: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = "/user/{userId}/exists", produces = "application/json;charset=UTF-8")
    @ResponseBody
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    def checkUserConnectionExists(@PathVariable('userId') String userId, Authentication authentication) {
        //check if current loggedInUser is connected with userId
        def response = [
                "success": true,
                "exists" : false,
                "error"  : ""
        ]

        try {
            userId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["exists"] = null
            response["error"] = "Invalid user id '${userId}'.".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def userConnectionDto = new UserConnectionDto(
                null,
                null,
                userId.toLong()
        )

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def exists = userConnectionIntegrationService.checkUserConnectionExists(userConnectionDto, authentication)
            response["exists"] = exists
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} " + validationException.getMessage())
            response["success"] = false
            response["exists"] = null
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to check if users are connected: ${exception.getMessage()}")
            response["success"] = false
            response["exists"] = null
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }


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
            response["error"] = "Invalid connection request id '${connectionRequestId}'.".toString()
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
            response["error"] = "Invalid connection userId '${connectedUserId}'".toString()
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
            log.trace("${userIdentifier} Failed to create user connection request: " + validationException.getMessage())
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
            response["error"] = "Invalid user id '${connectedUserId}'.".toString()
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
            log.trace("${userIdentifier} " + validationException.getMessage())
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
