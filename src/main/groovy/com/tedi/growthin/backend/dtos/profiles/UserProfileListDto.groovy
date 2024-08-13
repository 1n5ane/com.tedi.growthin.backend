package com.tedi.growthin.backend.dtos.profiles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserProfileListDto implements  Serializable{

    Integer totalPages

    List<UserProfileDto> userProfiles

    UserProfileListDto() {
        this.userProfiles = []
    }
}
