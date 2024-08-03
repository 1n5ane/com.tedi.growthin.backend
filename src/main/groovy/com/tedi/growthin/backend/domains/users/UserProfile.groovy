package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.media.Media
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "user_profiles")
class UserProfile implements Serializable {

    //TODO: this is wrong -> CHEEEEEECK
    @Id
    @Column(name = "user_id")
//  One user has exactly one profile -> profileId == user.id
    Long profileId

    @OneToOne
    @JoinColumn(name = "user_id")
    User user

    @Column
    String jobField

    @Column(name = "is_job_field_public", nullable = false)
    Boolean isJobFieldPublic = false

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_pic_id")
    Media profilePicMedia

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cv_document_id")
    Media cvDocumentMedia

    @Column(name = "is_cv_document_public", nullable = false)
    Boolean isCvDocumentPublic = false

    @Column
    String description

    @Column(name = "is_description_public", nullable = false)
    Boolean isDescriptionPublic = false

    @Column
    Integer age

    @Column(name = "is_age_public", nullable = false)
    Boolean isAgePublic = false

    @Column
    String education

    @Column(name = "is_education_public", nullable = false)
    Boolean isEducationPublic = false

    @Column(length = 50)
    String country

    @Column(name = "is_country_public", nullable = false)
    Boolean isCountryPublic = false

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    UserProfile() {}

    UserProfile(
            User user,
            String jobField,
            Media profilePicMedia,
            Media cvDocumentMedia,
            String description,
            Integer age,
            String education,
            String country,
            Boolean isJobFieldPublic = false,
            Boolean isCvDocumentPublic = false,
            Boolean isDescriptionPublic = false,
            Boolean isAgePublic = false,
            Boolean isEducationPublic = false,
            Boolean isCountryPublic = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null) {
        this.profileId = user.id
        this.jobField = jobField
        this.profilePicMedia = profilePicMedia
        this.cvDocumentMedia = cvDocumentMedia
        this.description = description
        this.age = age
        this.education = education
        this.country = country
        this.isJobFieldPublic = isJobFieldPublic
        this.isCvDocumentPublic = isCvDocumentPublic
        this.isDescriptionPublic = isDescriptionPublic
        this.isAgePublic = isAgePublic
        this.isEducationPublic = isEducationPublic
        this.isCountryPublic = isCountryPublic
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
        if (isAgePublic != that.isAgePublic) return false
        if (isCountryPublic != that.isCountryPublic) return false
        if (isCvDocumentPublic != that.isCvDocumentPublic) return false
        if (isDescriptionPublic != that.isDescriptionPublic) return false
        if (isEducationPublic != that.isEducationPublic) return false
        if (isJobFieldPublic != that.isJobFieldPublic) return false
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
        result = 31 * result + (isJobFieldPublic != null ? isJobFieldPublic.hashCode() : 0)
        result = 31 * result + (profilePicMedia != null ? profilePicMedia.hashCode() : 0)
        result = 31 * result + (cvDocumentMedia != null ? cvDocumentMedia.hashCode() : 0)
        result = 31 * result + (isCvDocumentPublic != null ? isCvDocumentPublic.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (isDescriptionPublic != null ? isDescriptionPublic.hashCode() : 0)
        result = 31 * result + (age != null ? age.hashCode() : 0)
        result = 31 * result + (isAgePublic != null ? isAgePublic.hashCode() : 0)
        result = 31 * result + (education != null ? education.hashCode() : 0)
        result = 31 * result + (isEducationPublic != null ? isEducationPublic.hashCode() : 0)
        result = 31 * result + (country != null ? country.hashCode() : 0)
        result = 31 * result + (isCountryPublic != null ? isCountryPublic.hashCode() : 0)
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
                ", isJobFieldPublic=" + isJobFieldPublic +
                ", profilePicMedia=" + profilePicMedia +
                ", cvDocumentMedia=" + cvDocumentMedia +
                ", isCvDocumentPublic=" + isCvDocumentPublic +
                ", description='" + description + '\'' +
                ", isDescriptionPublic=" + isDescriptionPublic +
                ", age=" + age +
                ", isAgePublic=" + isAgePublic +
                ", education='" + education + '\'' +
                ", isEducationPublic=" + isEducationPublic +
                ", country='" + country + '\'' +
                ", isCountryPublic=" + isCountryPublic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
