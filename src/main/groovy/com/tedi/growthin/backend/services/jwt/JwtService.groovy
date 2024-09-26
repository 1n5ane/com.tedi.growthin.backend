package com.tedi.growthin.backend.services.jwt

import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class JwtService {


    def static extractClaims(Jwt jwt){
        return jwt.claims
    }

    def static String extractUsername(Jwt jwt){
        return jwt.claims["sub"]
    }

    def static String extractName(Jwt jwt){
        return jwt.claims["name"]
    }

    def static String extractSurname(Jwt jwt){
        return jwt.claims["surname"]
    }

    def static String extractEmail(Jwt jwt){
        return jwt.claims["email"]
    }

    def static List extractAuthorities(Jwt jwt){
        return jwt.claims["authorities"] as List
    }

    def static Long extractAppUserId(Jwt jwt){
        return jwt.claims["appUserId"] as Long
    }

    def static Long extractAuthServerUserId(Jwt jwt){
        return jwt.claims["userId"] as Long
    }
}
