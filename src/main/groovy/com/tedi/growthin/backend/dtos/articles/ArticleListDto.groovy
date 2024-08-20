package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleListDto implements Serializable{
    Integer totalPages
    List<ArticleDto> articles

    ArticleListDto() {
        this.articles = []
    }
}
