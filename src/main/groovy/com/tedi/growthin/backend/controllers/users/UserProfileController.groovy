package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.profiles.UserProfileDto
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
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
        try {
            def createdUserProfileDto = userProfileIntegrationService.registerUserProfile(userProfileDto, authentication)
            response["profile"] = createdUserProfileDto
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to register user profile: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
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
        try {
            def updatedUserProfileDto = userProfileIntegrationService.updateUserProfile(userProfileDto, authentication)
            response["profile"] = updatedUserProfileDto
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to update user profile: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to update user profile: ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    //findById or username
    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def getUserProfile(@RequestParam(name = "id", required = false) String id,
                       @RequestParam(name = "username", required = false) String username,
                       @RequestParam(name = "ids", required = false) List<String> ids,
                       @RequestParam(name = "usernames", required = false) List<String> usernames,
                       Authentication authentication) {

        def response = [
                "success": true,
                "profile": null,
                "error"  : ""
        ]

        if (id != null || username != null)
            return getUserProfileByUsernameOrId(id, username, authentication)

        if ((ids != null && !ids.isEmpty()) || (usernames != null && !usernames.isEmpty()))
            return getMultipleUserProfilesByUsernamesOrIds(ids, usernames, authentication)

        response["success"] = false
        response["error"] = "No id, ids, username or usernames provided"
        return new ResponseEntity<>(response, HttpStatus.OK)

    }

    def getUserProfileByUsernameOrId(String id, String username, Authentication authentication) {

        def response = [
                "success": true,
                "profile": null,
                "error"  : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        Long userId = null
        if (id != null && !id.isEmpty()) {
            try {
                userId = id.toLong()
            } catch (NumberFormatException numberFormatException) {
                log.trace("${userIdentifier} Failed to get user profiles: Invalid id ${numberFormatException.getMessage()}")
                response["success"] = false
                response["error"] = "Invalid userr id '${id}'. Should be of type Long".toString()
                return new ResponseEntity<>(response, HttpStatus.OK)
            }
        }

        def userProfileDto = new UserProfileDto()
        userProfileDto.id = userId
        userProfileDto.username = username

        try {
            def fetchedUserProfileDto = userProfileIntegrationService.getUserProfileByIdOrUsername(userProfileDto, authentication)
            if (fetchedUserProfileDto == null) {
                response["error"] = "User profile was not found"
            } else {
                response["profile"] = fetchedUserProfileDto
            }
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to get user profile: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to get user profile: ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    def getMultipleUserProfilesByUsernamesOrIds(List<String> ids, List<String> usernames, Authentication authentication) {

        def response = [
                "success" : true,
                "profiles": [],
                "error"   : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        List<Long> userIds = []
        if (ids != null && !ids.isEmpty()) {
            try {
                ids.each { id ->
                    userIds.add(id.toLong())
                }
            } catch (NumberFormatException numberFormatException) {
                log.trace("${userIdentifier} Failed to get user profiles: Invalid id ${numberFormatException.getMessage()}")
                response["success"] = false
                response["error"] = "One or more user ids are invalid"
                return new ResponseEntity<>(response, HttpStatus.OK)
            }
        }

        try {
            def fetchedUserProfileDtoList = userProfileIntegrationService.getUserProfilesByIdsOrUsernames(userIds, usernames, authentication)
            if (fetchedUserProfileDtoList == null || fetchedUserProfileDtoList.isEmpty()) {
                response["error"] = "User profiles were not found"
            } else {
                response["profiles"] = fetchedUserProfileDtoList
            }
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to get user profiles: ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
            log.error("${userIdentifier} Failed to get user profiles: ${exception.getMessage()}")
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }


    @GetMapping(value = ["/all"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def listAllUserProfiles(@RequestParam(name = "page", defaultValue = "0") Integer page,
                            @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                            @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                            @RequestParam(name = "order", defaultValue = "desc") String order,
                            Authentication authentication) {
        def response = [
                "success"    : true,
                "hasNextPage": false,
                "profiles"   : null,
                "totalPages" : null,
                "error"      : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userProfileListDto = userProfileIntegrationService.findAllUserProfiles(page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userProfileListDto.totalPages
            response["totalPages"] = userProfileListDto.totalPages
            response["profiles"] = userProfileListDto.userProfiles
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all user profiles: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }

    @GetMapping(value = ["/search"], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    //search By username like
    def searchAllUserProfilesByUsername(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                        @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                        @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                        @RequestParam(name = "order", defaultValue = "desc") String order,
                                        @RequestParam(name = "username", required = false) String username,
                                        Authentication authentication) {
        def response = [
                "success"    : true,
                "hasNextPage": false,
                "profiles"   : null,
                "totalPages" : null,
                "error"      : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        if(username == null){
            log.trace("${userIdentifier} No username parameter provided")
            response["success"] = false
            response["error"] = "No username parameter provided"
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        try {
            def userProfileListDto = userProfileIntegrationService.searchAllUserProfilesByUsername(username, page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userProfileListDto.totalPages
            response["totalPages"] = userProfileListDto.totalPages
            response["profiles"] = userProfileListDto.userProfiles
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to search user profiles with username ${username}: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

}
