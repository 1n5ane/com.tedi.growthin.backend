package com.tedi.growthin.backend.dtos.connections

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.dtos.users.UserDto

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserConnectionListDto implements Serializable{
    //related user
    UserDto user

    Integer totalPages

    //list of maps -> [[userConnectionId: 0, user: ,createdAt],]
    def userConnections

    UserConnectionListDto() {
        this.userConnections = []
    }
}
