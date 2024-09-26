package com.tedi.growthin.backend.dtos.chats

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatRoomMessagesIsReadDto implements Serializable{

    List messageIds
    def senderId
    def receiverId
    Boolean isRead

    ChatRoomMessagesIsReadDto() {
        this.messageIds = []
    }

    ChatRoomMessagesIsReadDto(messageIds, senderId, receiverId, Boolean isRead) {
        this.messageIds = messageIds
        this.senderId = senderId
        this.receiverId = receiverId
        this.isRead = isRead
    }
}
