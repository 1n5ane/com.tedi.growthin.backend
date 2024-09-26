package com.tedi.growthin.backend.dtos.chats

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatRoomListDto implements Serializable{

    Integer totalPages
    List<HashMap> chatRooms

    ChatRoomListDto() {
        this.chatRooms = []
    }
}
