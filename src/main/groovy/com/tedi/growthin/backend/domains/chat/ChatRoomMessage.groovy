package com.tedi.growthin.backend.domains.chat

import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "chat_rooms_data")
class ChatRoomMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_messages_seq_gen")
    @SequenceGenerator(name = "chat_messages_seq_gen", sequenceName = "public.chat_messages_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_rooms", nullable = false)
    ChatRoom chatRoom

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = false)
    User sender

    @Column(nullable = false)
    String message

    @Column(nullable = false)
    Boolean isRead = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_media")
    Media media

    @Column(insertable = false, updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    ChatRoomMessage(Long id, ChatRoom chatRoom, User sender, String message, Boolean isRead, Media media, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.chatRoom = chatRoom
        this.sender = sender
        this.message = message
        this.isRead = isRead
        this.media = media
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ChatRoomMessage() {}

    @Override
    public String toString() {
        return "ChatRoomMessage{" +
                "id=" + id +
                ", chatRoomId=" + chatRoom?.id +
                ", sender=" + sender?.id +
                ", message='" + message + '\'' +
                ", isRead=" + isRead +
                ", media=" + media +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ChatRoomMessage that = (ChatRoomMessage) o

        if (chatRoom?.id != that.chatRoom?.id) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (isRead != that.isRead) return false
        if (media != that.media) return false
        if (message != that.message) return false
        if (sender?.id != that.sender?.id) return false
        if (updatedAt != that.updatedAt) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (chatRoom?.id != null ? chatRoom?.id?.hashCode() : 0)
        result = 31 * result + (sender?.id != null ? sender?.hashCode() : 0)
        result = 31 * result + (message != null ? message.hashCode() : 0)
        result = 31 * result + (isRead != null ? isRead.hashCode() : 0)
        result = 31 * result + (media != null ? media.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
