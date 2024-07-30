package com.tedi.growthin.backend.dtos.admins

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class AdminRequestListDto implements Serializable{

    Integer totalPages

    def requests

    AdminRequestListDto() {
        this.requests = []
    }
}
