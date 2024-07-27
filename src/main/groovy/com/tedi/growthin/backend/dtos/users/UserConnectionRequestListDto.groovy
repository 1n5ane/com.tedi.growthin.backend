package com.tedi.growthin.backend.dtos.users

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class UserConnectionRequestListDto implements Serializable{
    Integer totalPages
    def user //user the connection requests made to
    def requestedBy ////contains a list of maps -> ex. [["requestId":0, "user": UserDto, "createdAt":..., "updatedAt":...]]

    UserConnectionRequestListDto() {
        this.requestedBy = []
    }
}
