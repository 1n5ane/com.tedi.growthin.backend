package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
@Table(name = "user_connection_requests")
class UserConnectionRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_connection_request_id_seq_gen")
    @SequenceGenerator(name = "user_connection_request_id_seq_gen", sequenceName = "public.user_connection_request_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_user_id")
    User connectedUser

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "public.\"UserConnectionRequestStatus\"")
    UserConnectionRequestStatus status = UserConnectionRequestStatus.PENDING

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    UserConnectionRequest(User user, User connectedUser, UserConnectionRequestStatus status, OffsetDateTime updatedAt = null) {
        this.user = user
        this.connectedUser = connectedUser
        this.status = status
        this.updatedAt = updatedAt
    }

    UserConnectionRequest() {}

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserConnectionRequest that = (UserConnectionRequest) o

        if (connectedUser != that.connectedUser) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (status != that.status) return false
        if (updatedAt != that.updatedAt) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (connectedUser != null ? connectedUser.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "UserConnectionRequest{" +
                "id=" + id +
                ", user=" + user +
                ", connectedUser=" + connectedUser +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
