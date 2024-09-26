package com.tedi.growthin.backend.dtos.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class MediaTypeDto implements Serializable{
    def id

    @JsonProperty("type")
    String name

    MediaTypeDto() {}

    MediaTypeDto(id, String name) {
        this.id = id
        this.name = name
    }

    @Override
    public String toString() {
        return "MediaTypeDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
