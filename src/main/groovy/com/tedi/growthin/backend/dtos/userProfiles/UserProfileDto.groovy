package com.tedi.growthin.backend.dtos.userProfiles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.dtos.media.MediaDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserProfileDto implements Serializable {

    def id //always profileId == userId
    String jobField
    Boolean isJobFieldPublic
    String description
    Boolean isDescriptionPublic
    def age
    Boolean isAgePublic
    String education
    Boolean isEducationPublic
    String country
    Boolean isCountryPublic
    OffsetDateTime createdAt
    OffsetDateTime updatedAt
    MediaDto profilePic
    Boolean isCvDocumentPublic
    MediaDto cvDocument

    UserProfileDto() {}

    UserProfileDto(Long id,
                   String jobField,
                   MediaDto profilePic,
                   MediaDto cvDocument,
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
        this.id = id
        this.jobField = jobField
        this.profilePic = profilePic
        this.cvDocument = cvDocument
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
}
