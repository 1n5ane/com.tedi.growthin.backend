package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.enums.AdminRequestStatus
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
import jakarta.persistence.OneToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.Type

import java.time.OffsetDateTime

@Entity
@Table(name = "admin_requests")
class UserAdminRequest implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admin_requests_seq_gen")
    @SequenceGenerator(name = "admin_requests_seq_gen", sequenceName = "public.admin_requests_seq", allocationSize = 1)
    Long id

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curated_by_user_id")
    User curatedByAdmin

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "public.\"AdminRequestStatus\"")
    AdminRequestStatus status = AdminRequestStatus.PENDING

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    UserAdminRequest(User user, User curatedByAdmin = null, AdminRequestStatus status = AdminRequestStatus.PENDING) {
        this.user = user
        this.curatedByAdmin = curatedByAdmin
        this.status = status
    }

    UserAdminRequest(){}

    @Override
    public String toString() {
        return "UserAdminRequest{" +
                "id=" + id +
                ", user=" + user +
                ", curatedByAdmin=" + curatedByAdmin +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserAdminRequest that = (UserAdminRequest) o

        if (createdAt != that.createdAt) return false
        if (curatedByAdmin != that.curatedByAdmin) return false
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
        result = 31 * result + (curatedByAdmin != null ? curatedByAdmin.hashCode() : 0)
        result = 31 * result + (status != null ? status.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
