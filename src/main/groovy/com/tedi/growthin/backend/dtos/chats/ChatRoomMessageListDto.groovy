package com.tedi.growthin.backend.dtos.chats

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.dtos.users.UserDto

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatRoomMessageListDto implements Serializable{

    Integer totalPages
    UserDto user
    List<HashMap> chatRoomMessages

    ChatRoomMessageListDto() {
        this.chatRoomMessages = []
    }
}
