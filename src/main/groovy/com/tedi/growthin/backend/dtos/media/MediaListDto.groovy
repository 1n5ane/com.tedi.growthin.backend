package com.tedi.growthin.backend.dtos.media

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class MediaListDto implements Serializable{
    //todo: for paging
    Integer totalPages
//    ....
}
