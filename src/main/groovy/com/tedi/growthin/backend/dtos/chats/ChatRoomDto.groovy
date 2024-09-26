package com.tedi.growthin.backend.dtos.chats

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatRoomDto implements Serializable{

    def id
    UserDto relatedUser1
    UserDto relatedUser2

    OffsetDateTime createdAt

    ChatRoomDto() {}

    ChatRoomDto(id, UserDto relatedUser1, UserDto relatedUser2, OffsetDateTime createdAt = null) {
        this.id = id
        this.relatedUser1 = relatedUser1
        this.relatedUser2 = relatedUser2
        this.createdAt = createdAt
    }


    @Override
    public String toString() {
        return "ChatRoomDto{" +
                "id=" + id +
                ", relatedUser1=" + relatedUser1 +
                ", relatedUser2=" + relatedUser2 +
                ", createdAt=" + createdAt +
                '}';
    }
}
