package com.tedi.growthin.backend.dtos

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserDto implements Serializable {
    def id
    String username
    String password
    String name
    String surname
    String email
    List<String> authorities
    String phone
    String country
    String area

    UserDto(def id, String username, String password, String name, String surname, String email, List<String> authorities = ['ROLE_USER'], String phone = null, String country = null, String area = null) {
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
    }

    UserDto(def id, String username, String name, String surname, String email, List<String> authorities = ['ROLE_USER'], String phone = null, String country = null, String area = null) {
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
    }

    UserDto(String username, String name, String surname, String email, List<String> authorities = ['ROLE_USER'], String phone = null, String country = null, String area = null) {
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
    }

    UserDto(){}

    @Override
    public String toString() {
        return "UserDto{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                ", phone='" + phone + '\'' +
                ", country='" + country + '\'' +
                ", area='" + area + '\'' +
                '}';
    }
}
