package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleCommentListDto implements Serializable{
    Integer totalPages
    List<ArticleCommentDto> articleComments

    ArticleCommentListDto() {
        this.articleComments = []
    }
}
