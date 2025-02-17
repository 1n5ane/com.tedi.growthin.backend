package com.tedi.growthin.backend.domains.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "user_connections")
class UserConnection implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_connection_id_seq_gen")
    @SequenceGenerator(name = "user_connection_id_seq_gen", sequenceName = "public.user_connection_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_user_id")
    User connectedUser

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    UserConnection() {}

    UserConnection(Long id, User user, User connectedUser, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.connectedUser = connectedUser
        this.createdAt = createdAt
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserConnection that = (UserConnection) o

        if (connectedUser != that.connectedUser) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (connectedUser != null ? connectedUser.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "UserConnection{" +
                "id=" + id +
                ", user=" + user +
                ", connectedUser=" + connectedUser +
                ", createdAt=" + createdAt +
                '}';
    }
}
