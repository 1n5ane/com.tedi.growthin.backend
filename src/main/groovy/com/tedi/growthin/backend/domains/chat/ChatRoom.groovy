package com.tedi.growthin.backend.domains.chat

import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "chat_rooms", uniqueConstraints = [
        @UniqueConstraint(
                name = "user_chatroom_uniqueness",
                columnNames = ["related_user_id1", "related_user_id2"]
        )
]) // 2 users can only have one chat
class ChatRoom implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_room_seq_gen")
    @SequenceGenerator(name = "chat_room_seq_gen", sequenceName = "public.chat_room_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id1")
    User user1

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id2")
    User user2

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "chatRoom")
    @OrderBy(value = "id DESC")
    List<ChatRoomMessage> chatRoomMessages


    @Override
    public String toString() {
        return "ChatRoom{" +
                "id=" + id +
                ", user1=" + user1 +
                ", user2=" + user2 +
                ", createdAt=" + createdAt +
                ", chatRoomMessages=" + chatRoomMessages +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ChatRoom chatRoom = (ChatRoom) o

        if (chatRoomMessages != chatRoom.chatRoomMessages) return false
        if (createdAt != chatRoom.createdAt) return false
        if (id != chatRoom.id) return false
        if (user1 != chatRoom.user1) return false
        if (user2 != chatRoom.user2) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user1 != null ? user1.hashCode() : 0)
        result = 31 * result + (user2 != null ? user2.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (chatRoomMessages != null ? chatRoomMessages.hashCode() : 0)
        return result
    }
}
