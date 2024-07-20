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

@Entity
@Table(name = "chat_rooms")
class ChatRoomMessage implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_messages_seq_gen")
    @SequenceGenerator(name = "chat_messages_seq_gen", sequenceName = "public.chat_messages_seq")
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_rooms", nullable = false)
    ChatRoom chatRoom

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    User sender

    @Column(nullable = false)
    String message

    @Column(nullable = false)
    Boolean isRead = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media")
    Media media

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt


    @Override
    public String toString() {
        return "ChatRoomMessage{" +
                "id=" + id +
                ", chatRoom=" + chatRoom +
                ", sender=" + sender +
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

        if (chatRoom != that.chatRoom) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (isRead != that.isRead) return false
        if (media != that.media) return false
        if (message != that.message) return false
        if (sender != that.sender) return false
        if (updatedAt != that.updatedAt) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (chatRoom != null ? chatRoom.hashCode() : 0)
        result = 31 * result + (sender != null ? sender.hashCode() : 0)
        result = 31 * result + (message != null ? message.hashCode() : 0)
        result = 31 * result + (isRead != null ? isRead.hashCode() : 0)
        result = 31 * result + (media != null ? media.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
