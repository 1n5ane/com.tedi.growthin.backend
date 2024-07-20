package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.media.Media
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "user_profiles")
class UserProfile implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id")
//  One user has exactly one profile -> profileId == user.id
    Long profileId

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user

    @Column
    String jobField


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_pic_id")
    Media profilePicMedia

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_document_id")
    Media cvDocumentMedia

    @Column
    String description

    @Column
    Integer age

    @Column
    String education

    @Column(length = 50)
    String country

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt

    UserProfile() {}

    UserProfile(User user, String jobField, Media profilePicMedia, Media cvDocumentMedia, String description, Integer age, String education, String country, Date createdAt, Date updatedAt = null) {
        this.profileId = user.id
        this.user = user
        this.jobField = jobField
        this.profilePicMedia = profilePicMedia
        this.cvDocumentMedia = cvDocumentMedia
        this.description = description
        this.age = age
        this.education = education
        this.country = country
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserProfile that = (UserProfile) o

        if (age != that.age) return false
        if (country != that.country) return false
        if (createdAt != that.createdAt) return false
        if (cvDocumentMedia != that.cvDocumentMedia) return false
        if (description != that.description) return false
        if (education != that.education) return false
        if (jobField != that.jobField) return false
        if (profileId != that.profileId) return false
        if (profilePicMedia != that.profilePicMedia) return false
        if (updatedAt != that.updatedAt) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (profileId != null ? profileId.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (jobField != null ? jobField.hashCode() : 0)
        result = 31 * result + (profilePicMedia != null ? profilePicMedia.hashCode() : 0)
        result = 31 * result + (cvDocumentMedia != null ? cvDocumentMedia.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (age != null ? age.hashCode() : 0)
        result = 31 * result + (education != null ? education.hashCode() : 0)
        result = 31 * result + (country != null ? country.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileId=" + profileId +
                ", user=" + user +
                ", jobField='" + jobField + '\'' +
                ", profilePicMedia=" + profilePicMedia +
                ", cvDocumentMedia=" + cvDocumentMedia +
                ", description='" + description + '\'' +
                ", age=" + age +
                ", education='" + education + '\'' +
                ", country='" + country + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
