package com.tedi.growthin.backend.dtos.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class MediaDto implements Serializable {

    def id
    def userId

    @JsonProperty("mediaType")
    MediaTypeDto mediaTypeDto

    byte[] data
    Boolean isDeleted
    OffsetDateTime createdAt

    MediaDto() {
        this.isDeleted = null
    }

    MediaDto(id, userId, MediaTypeDto mediaTypeDto, byte[] data, Boolean isDeleted = null, OffsetDateTime createdAt = null) {
        this.id = id
        this.userId = userId
        this.mediaTypeDto = mediaTypeDto
        this.data = data
        this.isDeleted = isDeleted
        this.createdAt = createdAt
    }


    @Override
    public String toString() {
        return "MediaDto{" +
                "id=" + id +
                ", userId=" + userId +
                ", mediaTypeDto=" + mediaTypeDto +
                ", data=[...]"+
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                '}';
    }
}
