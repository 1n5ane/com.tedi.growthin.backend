package com.tedi.growthin.backend.domains.users

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Embeddable
@Table(name = "users")
class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_id_seq_gen")
    @SequenceGenerator(name = "users_id_seq_gen", sequenceName = "public.users_id_seq", allocationSize = 1)
    Long id

    @Column(unique = true, nullable = false, length = 25)
    String username

    @Column(unique = true, nullable = false, length = 255)
    String email

    @Column(nullable = false, length = 255)
    String firstName

    @Column(nullable = false, length = 255)
    String lastName

    @Column(length = 255)
    String phone

    @Column(length = 255)
    String area

    @Column(nullable = false)
    Boolean isAdmin = false

    @Column(length = 100)
    String country

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    User(String username, String email, String firstName, String lastName, String phone, String area, String country, Boolean isAdmin = false, OffsetDateTime updatedAt = null) {
        this.username = username
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.phone = phone
        this.area = area
        this.country = country
        this.isAdmin = isAdmin
        this.updatedAt = updatedAt
    }

    User(Long id, String username, String email, String firstName, String lastName, String phone, String area, String country, Boolean isAdmin = false, OffsetDateTime updatedAt = null) {
        this.id = id
        this.username = username
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.phone = phone
        this.area = area
        this.country = country
        this.isAdmin = isAdmin
        this.updatedAt = updatedAt
    }

    User(Long id, String username, String email, String firstName, String lastName, String phone, String area, String country, Boolean isAdmin, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id
        this.username = username
        this.email = email
        this.firstName = firstName
        this.lastName = lastName
        this.phone = phone
        this.area = area
        this.country = country
        this.isAdmin = isAdmin
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    User() {}

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        User user = (User) o

        if (area != user.area) return false
        if (country != user.country) return false
        if (createdAt != user.createdAt) return false
        if (email != user.email) return false
        if (firstName != user.firstName) return false
        if (id != user.id) return false
        if (isAdmin != user.isAdmin) return false
        if (lastName != user.lastName) return false
        if (phone != user.phone) return false
        if (updatedAt != user.updatedAt) return false
        if (username != user.username) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (username != null ? username.hashCode() : 0)
        result = 31 * result + (email != null ? email.hashCode() : 0)
        result = 31 * result + (firstName != null ? firstName.hashCode() : 0)
        result = 31 * result + (lastName != null ? lastName.hashCode() : 0)
        result = 31 * result + (phone != null ? phone.hashCode() : 0)
        result = 31 * result + (area != null ? area.hashCode() : 0)
        result = 31 * result + (isAdmin != null ? isAdmin.hashCode() : 0)
        result = 31 * result + (country != null ? country.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", area='" + area + '\'' +
                ", isAdmin=" + isAdmin +
                ", country='" + country + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
