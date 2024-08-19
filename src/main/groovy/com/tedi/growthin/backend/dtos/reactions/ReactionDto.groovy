package com.tedi.growthin.backend.dtos.reactions

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ReactionDto implements Serializable {

    def id
    String alias
    byte[] image
    OffsetDateTime createdAt

    ReactionDto() {}

    ReactionDto(def id) {
        this.id = id
    }

    ReactionDto(def id, String alias) {
        this.id = id
        this.alias = alias
    }

    ReactionDto(def id, String alias, byte[] image, OffsetDateTime createdAt = null) {
        this.id = id
        this.alias = alias
        this.image = image
        this.createdAt = createdAt
    }


    @Override
    public String toString() {
        return "ReactionDto{" +
                "id=" + id +
                ", alias='" + alias + '\'' +
                ", image=" + image ? "[...]" : null +
                ", createdAt=" + createdAt +
                '}';
    }
}
