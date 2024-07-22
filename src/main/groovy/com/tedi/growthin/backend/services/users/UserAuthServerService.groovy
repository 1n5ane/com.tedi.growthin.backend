package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.configuration.EndpointsConfiguration
import com.tedi.growthin.backend.dtos.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserAuthServerService {

    @Autowired
    RestTemplate restTemplate

    @Autowired
    EndpointsConfiguration endpointsConfiguration

    def registerUser(UserDto userDto)throws Exception {
        def userUrl = endpointsConfiguration.getAuthServerBaseUserEndpoint()
        def res = restTemplate.postForEntity(userUrl,userDto,HashMap.class)
    }

    def checkUserExistsByUsername(String username) throws Exception {
        def userExistsUrl = endpointsConfiguration.getAuthServerUserExistsEndpoint()
        URI uri = UriComponentsBuilder
                .fromHttpUrl(userExistsUrl)
                .queryParam("username", username)
                .build()
                .toUri()

        def resp = restTemplate.getForObject(uri, HashMap.class)
        return resp["exists"]
    }

    def checkUserExistsByEmail(String email) throws Exception {
        def userExistsUrl = endpointsConfiguration.getAuthServerUserExistsEndpoint()
        URI uri = UriComponentsBuilder
                .fromHttpUrl(userExistsUrl)
                .queryParam("email", email)
                .build()
                .toUri()

        def resp = restTemplate.getForObject(uri, HashMap.class)
        return resp["exists"]
    }
}
