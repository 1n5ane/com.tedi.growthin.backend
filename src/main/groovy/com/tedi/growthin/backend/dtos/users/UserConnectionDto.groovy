package com.tedi.growthin.backend.dtos.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserConnectionDto implements Serializable{
    def id
    def userId
    def connectedUserId
    OffsetDateTime createdAt

    UserConnectionDto() {}

    UserConnectionDto(id, userId, connectedUserId, OffsetDateTime createdAt = null) {
        this.id = id
        this.userId = userId
        this.connectedUserId = connectedUserId
        this.createdAt = createdAt
    }
}
