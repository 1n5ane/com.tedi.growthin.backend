package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.media.MediaDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleMediaDto implements Serializable {
    def id
    def articleId
    @JsonProperty("media")
    MediaDto mediaDto
    Integer order
    OffsetDateTime createdAt

    ArticleMediaDto() {}

    ArticleMediaDto(id, articleId, MediaDto mediaDto, Integer order, OffsetDateTime createdAt = null) {
        this.id = id
        this.articleId = articleId
        this.mediaDto = mediaDto
        this.order = order
        this.createdAt = createdAt
    }

    @Override
    public String toString() {
        return "ArticleMediaDto{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", mediaDto=" + mediaDto +
                ", order=" + order +
                ", createdAt=" + createdAt +
                '}';
    }
}
