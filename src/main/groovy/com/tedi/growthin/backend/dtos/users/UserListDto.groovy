package com.tedi.growthin.backend.dtos.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.stereotype.Component

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserListDto implements Serializable {
    Integer totalPages

    List<UserDto> users

    UserListDto() {
        this.users = []
    }
}
