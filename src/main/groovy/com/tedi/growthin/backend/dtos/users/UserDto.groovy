package com.tedi.growthin.backend.dtos.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.tedi.growthin.backend.dtos.media.MediaDto

import java.time.OffsetDateTime


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserDto implements Serializable {
    def id
    String username
    String password
    String name
    String surname
    String email
    Boolean isEmailPublic
    List<String> authorities
    String phone
    Boolean isPhonePublic
    String country
    Boolean isCountryPublic
    String area
    Boolean isAreaPublic
    MediaDto profilePic
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    Boolean locked

    UserDto(def id,
            String username,
            String name,
            String surname,
            String email,
            List<String> authorities,
            String phone,
            String country,
            String area,
            Boolean locked,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt,
            Boolean isPhonePublic = false,
            Boolean isEmailPublic = false,
            Boolean isCountryPublic = false,
            Boolean isAreaPublic = false,
            MediaDto profilePicMedia = null) {
        this.id = id
        this.username = username
        this.name = name
        this.surname = surname
        this.email = email
        this.authorities = authorities
        this.phone = phone
        this.country = country
        this.area = area
        this.locked = locked
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.isPhonePublic = isPhonePublic
        this.isEmailPublic = isEmailPublic
        this.isCountryPublic = isCountryPublic
        this.isAreaPublic = isAreaPublic
        this.profilePic = profilePicMedia
    }

    UserDto(def id,
            String username,
            String password,
            String name,
            String surname,
            String email,
            List<String> authorities = ['ROLE_USER'],
            String phone = null,
            String country = null,
            String area = null,
            Boolean isEmailPublic = false,
            Boolean isPhonePublic = false,
            Boolean isCountryPublic = false,
            Boolean isAreaPublic = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null,
            MediaDto profilePicMedia = null) {
        this.id = id
        this.username = username
        this.password = password
        this.name = name
        this.surname = surname
        this.email = email
        this.authorities = authorities
        this.phone = phone
        this.country = country
        this.area = area
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.isEmailPublic = isEmailPublic
        this.isPhonePublic = isPhonePublic
        this.isCountryPublic = isCountryPublic
        this.isAreaPublic = isAreaPublic
    }

    UserDto(def id,
            String username,
            String name,
            String surname,
            String email,
            List<String> authorities = ['ROLE_USER'],
            String phone = null,
            String country = null,
            String area = null,
            Boolean isEmailPublic = false,
            Boolean isPhonePublic = false,
            Boolean isCountryPublic = false,
            Boolean isAreaPublic = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null,
            MediaDto profilePicMedia = null) {
        this.id = id
        this.username = username
        this.password = null
        this.name = name
        this.surname = surname
        this.email = email
        this.authorities = authorities
        this.phone = phone
        this.country = country
        this.area = area
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.isEmailPublic = isEmailPublic
        this.isPhonePublic = isPhonePublic
        this.isCountryPublic = isCountryPublic
        this.isAreaPublic = isAreaPublic
        this.profilePic = profilePicMedia
    }

    UserDto(String username,
            String name,
            String surname,
            String email,
            List<String> authorities = ['ROLE_USER'],
            String phone = null,
            String country = null,
            String area = null,
            Boolean isEmailPublic = false,
            Boolean isPhonePublic = false,
            Boolean isCountryPublic = false,
            Boolean isAreaPublic = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null,
            MediaDto profilePicMedia = null) {
        this.id = null
        this.username = username
        this.password = null
        this.name = name
        this.surname = surname
        this.email = email
        this.authorities = authorities
        this.phone = phone
        this.country = country
        this.area = area
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.isEmailPublic = isEmailPublic
        this.isPhonePublic = isPhonePublic
        this.isCountryPublic = isCountryPublic
        this.isAreaPublic = isAreaPublic
    }

    UserDto() {}


    @Override
    public String toString() {
        return "UserDto{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", isEmailPublic=" + isEmailPublic +
                ", authorities=" + authorities +
                ", phone='" + phone + '\'' +
                ", isPhonePublic=" + isPhonePublic +
                ", country='" + country + '\'' +
                ", isCountryPublic=" + isCountryPublic +
                ", area='" + area + '\'' +
                ", isAreaPublic=" + isAreaPublic +
                ", profilePic=" + profilePic +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", locked=" + locked +
                '}';
    }
}
