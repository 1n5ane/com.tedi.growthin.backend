package com.tedi.growthin.backend.configuration

import jakarta.annotation.PostConstruct
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "application.endpoints")
class EndpointsConfiguration {
    String authServer

    @PostConstruct
    def initialize(){
        if(authServer.endsWith("/"))
            authServer = authServer.substring(0, authServer.length() - 1)

    }

    String getAuthServerBaseUserEndpoint(){
        return authServer+"/user"
    }

    String getAuthServerUserExistsEndpoint(){
        return authServer+"/user/exists"
    }

    String getAuthServerUserSearchByUsernameEndpoint(){
        return authServer+"/user/search"
    }
}
