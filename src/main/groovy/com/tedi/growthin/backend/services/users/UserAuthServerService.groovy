package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.configuration.EndpointsConfiguration
import com.tedi.growthin.backend.dtos.users.UserDto
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Service
class UserAuthServerService {

    @Autowired
    RestTemplate restTemplate

    @Autowired
    EndpointsConfiguration endpointsConfiguration

    def registerUser(UserDto userDto) throws Exception {
        def userUrl = endpointsConfiguration.getAuthServerBaseUserEndpoint()
        def res = restTemplate.postForEntity(userUrl, userDto, HashMap.class)
        return res.getBody()
    }

    def updateUser(UserDto userDto, String jwtToken) throws Exception {
        def userUrl = endpointsConfiguration.getAuthServerBaseUserEndpoint()

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        headers.setContentType(MediaType.APPLICATION_JSON); // Ensure Content-Type is set correctly

        HttpEntity httpEntity = new HttpEntity(userDto, headers)
        ResponseEntity<HashMap> response = restTemplate.exchange(
                userUrl,
                HttpMethod.PUT,
                httpEntity,
                HashMap.class
        )
        return response.getBody()
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
