package com.tedi.growthin.backend.domains.media

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
@Table(name = "media")
class Media implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "media_id_seq_gen")
    @SequenceGenerator(name = "media_id_seq_gen", sequenceName = "public.media_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users")
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_media_types")
    MediaType mediaType

    @Column(nullable = false)
    byte[] data

    @Column
    Boolean isDeleted = false

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    Media(Long id, User user, MediaType mediaType, byte[] data, Boolean isDeleted, Date createdAt) {
        this.id = id
        this.user = user
        this.mediaType = mediaType
        this.data = data
        this.isDeleted = isDeleted
        this.createdAt = createdAt
    }

    Media(Long id, User user, MediaType mediaType, byte[] data, Boolean isDeleted = false) {
        this.id = id
        this.user = user
        this.mediaType = mediaType
        this.data = data
        this.isDeleted = isDeleted
    }

    Media(User user, MediaType mediaType, byte[] data, Boolean isDeleted = false) {
        this.user = user
        this.mediaType = mediaType
        this.data = data
        this.isDeleted = isDeleted
    }

    Media() {}

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Media media = (Media) o

        if (createdAt != media.createdAt) return false
        if (!Arrays.equals(data, media.data)) return false
        if (id != media.id) return false
        if (isDeleted != media.isDeleted) return false
        if (mediaType != media.mediaType) return false
        if (user != media.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (mediaType != null ? mediaType.hashCode() : 0)
        result = 31 * result + (data != null ? Arrays.hashCode(data) : 0)
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "Media{" +
                "id=" + id +
                ", user=" + user +
                ", mediaType=" + mediaType +
                ", data=" + Arrays.toString(data) +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                '}';
    }
}
