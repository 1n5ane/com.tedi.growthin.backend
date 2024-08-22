package com.tedi.growthin.backend.dtos.notifications

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class NotificationTypeDto implements Serializable{
    def id

    @JsonProperty("type")
    String name

    NotificationTypeDto() {}

    NotificationTypeDto(id, String name) {
        this.id = id
        this.name = name
    }


    @Override
    public String toString() {
        return "NotificationTypeDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
