package com.tedi.growthin.backend.dtos.admins

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.domains.enums.AdminRequestStatus

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class AdminRequestDto implements Serializable{

    def id
    def userId
    def curatedByAdminId
    AdminRequestStatus status
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    AdminRequestDto() {}

    AdminRequestDto(id, userId, curatedByAdminId, AdminRequestStatus status, OffsetDateTime createdAt, OffsetDateTime updatedAt = null) {
        this.id = id
        this.userId = userId
        this.curatedByAdminId = curatedByAdminId
        this.status = status
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }
}
