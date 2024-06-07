package com.tedi.growthin.backend.controllers

import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

//TODO: implement register flow, getUserDetails etc...(CRUD)
//      on user registration (user details will be held in resource server db (except password))
//      and then on auth server...

@RestController
@RequestMapping("/api/v1")
@Slf4j
class UserController {

    @GetMapping(value = "/user/{id}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    def getUser(@PathVariable("id") String id, Authentication authentication) {
        def response = ["success": true,
                        "user"   : null,
                        "error"  : ""]
        return new ResponseEntity<>(response, HttpStatus.OK)
    }
}
