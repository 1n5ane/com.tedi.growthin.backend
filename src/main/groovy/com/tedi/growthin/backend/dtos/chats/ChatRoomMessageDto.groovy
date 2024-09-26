package com.tedi.growthin.backend.dtos.chats

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.media.MediaDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ChatRoomMessageDto implements Serializable{

    def id
    def chatRoomId
    def senderId
    def receiverId
    String message
    Boolean isRead
    @JsonProperty(value = "media")
    MediaDto mediaDto
    OffsetDateTime createdAt
    OffsetDateTime udpatedAt

    ChatRoomMessageDto() {}

    ChatRoomMessageDto(id, chatRoomId, senderId, receiverId, String message, Boolean isRead, MediaDto mediaDto, OffsetDateTime createdAt, OffsetDateTime udpatedAt) {
        this.id = id
        this.chatRoomId = chatRoomId
        this.senderId = senderId
        this.receiverId = receiverId
        this.message = message
        this.isRead = isRead
        this.mediaDto = mediaDto
        this.createdAt = createdAt
        this.udpatedAt = udpatedAt
    }

    @Override
    public String toString() {
        return "ChatRoomMessageDto{" +
                "id=" + id +
                ", chatRoomId=" + chatRoomId +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", mediaDto=" + mediaDto +
                ", createdAt=" + createdAt +
                ", udpatedAt=" + udpatedAt +
                '}';
    }
}
