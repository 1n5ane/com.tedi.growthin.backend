package com.tedi.growthin.backend.controllers.admins

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
import com.tedi.growthin.backend.dtos.admins.AdminRequestDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.AdminIntegrationService
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
@RequestMapping("/api/v1/admin/user")
@Slf4j
class AdminUserController {

    @Autowired
    AdminIntegrationService adminIntegrationService


    @GetMapping(value = "/{id}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def getUser(@PathVariable("id") String id, Authentication authentication) {
        def response = ["success": true,
                        "user"   : null,
                        "error"  : ""]

        Long userId
        try {
            userId = id.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid user id '${id}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }


        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        UserDto userDto = new UserDto()
        userDto.id = userId
        try {
            def user = adminIntegrationService.getUser(userDto, authentication)
            response["user"] = user
            if (!user) {
                response["success"] = false
                response["error"] = "User with id ${userId} was not found".toString()
                //if user not found -> return 404
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND)
            }
        } catch (ValidationException validationException) {
            log.error("${userIdentifier} Failed to get user '${userId}': ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to get user '${userId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    //list all user details
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
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userListDto = adminIntegrationService.findAllUsers(page, pageSize, sortBy, order, authentication)
            response["hasNextPage"] = (page + 1) < userListDto.totalPages
            response["totalPages"] = userListDto.totalPages
            response["users"] = userListDto.users
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all users: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }


    private def restrictUserAccount(String id, Authentication authentication, Boolean restrict = true) {
        def response = ["success": true,
                        "error"  : ""]

        Long userId
        try {
            userId = id.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid user id '${id}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        UserDto userDto = new UserDto()
        userDto.id = userId

        try {
            adminIntegrationService.updateUserLocked(userDto, authentication, restrict)
            log.info("${userIdentifier} Successfully set restriction to '${restrict}' for user with id ${userId}")
        } catch (ValidationException validationException) {
            log.error("${userIdentifier} Failed to set restriction to '${restrict}' for user with id '${userId}': ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to set restriction to '${restrict}' for user with id '${userId}'': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = "/{id}/ban", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def banUserAccountByUserId(@PathVariable("id") String id, Authentication authentication) {
        //update user details set locked to true in auth server to ban user
        return restrictUserAccount(id, authentication, true)
    }


    @PostMapping(value = "/{id}/unban", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def unbanUserAccountByUserId(@PathVariable("id") String id, Authentication authentication) {
        return restrictUserAccount(id, authentication, false)
    }

    @GetMapping(value = "/banned", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def listAllBannedUsers(@RequestParam(name = "page", defaultValue = "0") Integer page,
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
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userListDto = adminIntegrationService.findAllRestrictedUsers(page, pageSize, sortBy, order, true)
            response["hasNextPage"] = (page + 1) < userListDto.totalPages
            response["totalPages"] = userListDto.totalPages
            response["users"] = userListDto.users
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list all banned users: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }

    @GetMapping(value = "/requests", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def listAllUserAdminRequestsByStatus(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                         @RequestParam(name = "size", defaultValue = "15") Integer pageSize,
                                         @RequestParam(name = "sortBy", defaultValue = "id") String sortBy,
                                         @RequestParam(name = "order", defaultValue = "desc") String order,
                                         @RequestParam(name = "status", defaultValue = "PENDING") String status,
                                         Authentication authentication) {

        def response = [
                "success"    : true,
                "hasNextPage": false,
                "status"     : null,
                "requests"   : [], //contains a list of maps -> ex. [["requestId":0, "user": UserDto, "createdAt":..., "updatedAt":...]]
                "totalPages" : null,
                "error"      : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        def enumStatus
        try {
            enumStatus = Enum.valueOf(AdminRequestStatus.class, status)
        } catch (IllegalArgumentException ignored) {
            log.trace("${userIdentifier} Invalid status '${status}' on list all admin requests.")
            response["success"] = false
            response["error"] = "Invalid status ${status} .Status can be PENDING, ACCEPTED or DECLINED".toString()
            //it's GSTRING
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        try {
            def adminRequestListDto = adminIntegrationService.findAllAdminRequestsByStatus(
                    enumStatus,
                    page,
                    pageSize,
                    sortBy,
                    order,
                    authentication
            )
            response["hasNextPage"] = (page + 1) < adminRequestListDto.totalPages
            response["totalPages"] = adminRequestListDto.totalPages
            response["status"] = enumStatus
            response["requests"] = adminRequestListDto.requests
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} ${validationException.getMessage()}")
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to list '${status}' user admin requests: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PutMapping(value = "/requests/{requestId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def updateUserAdminRequestStatus(@PathVariable("requestId") String requestId,
                                     @RequestBody AdminRequestDto adminRequestDto,
                                     Authentication authentication) {
        //by requestId
        //grants or not admin privileges to user
        // accept or decline request and give or remove admin privileges

        def response = [
                "success"     : true,
                "adminRequest": null,
                "error"       : ""
        ]

        try {
            requestId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid user admin request id '${requestId}'.".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }
        adminRequestDto.id = requestId.toLong()
        adminRequestDto.userId = null
        adminRequestDto.curatedByAdminId = null

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[adminId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def userAdminRequest = adminIntegrationService.updateUserAdminRequest(adminRequestDto, authentication)
            if(!userAdminRequest){
                log.trace("${userIdentifier} User admin request with id '${adminRequestDto.id}' was not updated. No changes were made")
            }
            response['adminRequest'] = userAdminRequest
        } catch (ValidationException validationException) {
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to update user admin request with id ${adminRequestDto.id}: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }
}
