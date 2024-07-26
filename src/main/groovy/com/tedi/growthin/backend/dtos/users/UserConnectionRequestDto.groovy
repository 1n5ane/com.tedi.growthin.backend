package com.tedi.growthin.backend.dtos.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserConnectionRequestDto implements Serializable {
    def id
    def userId
    def connectedUserId
    UserConnectionRequestStatus status
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    UserConnectionRequestDto(){}

    UserConnectionRequestDto(id,
                             userId,
                             connectedUserId,
                             UserConnectionRequestStatus status,
                             OffsetDateTime createdAt = null,
                             OffsetDateTime updatedAt = null) {
        this.id = id
        this.userId = userId
        this.connectedUserId = connectedUserId
        this.status = status
        this.createdAt = createdAt
        this.updatedAt = updatedAt

    }
}
