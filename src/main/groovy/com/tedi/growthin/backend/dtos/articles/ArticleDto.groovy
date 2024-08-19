package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.domains.enums.PublicStatus
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleDto implements Serializable {
    def id

    @JsonProperty(value = "user")
    UserDto userDto

    String title
    String body
    PublicStatus publicStatus
    Boolean isDeleted
    OffsetDateTime createdAt
    OffsetDateTime updatedAt
    List<ArticleMediaDto> articleMedias

    List<ArticleCommentDto> articleComments
    List<ArticleReactionDto> articleReactions

    ArticleDto() {
        this.articleComments = []
        this.articleReactions = []
        this.articleMedias = []
    }

    ArticleDto(id, UserDto userDto, String title, String body, PublicStatus publicStatus, List<ArticleMediaDto> articleMedias = [], Boolean isDeleted = false, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.userDto = userDto
        this.title = title
        this.body = body
        this.publicStatus = publicStatus
        this.isDeleted = isDeleted
        this.articleMedias = articleMedias
        this.articleReactions = []
        this.articleComments = []
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ArticleDto(id, UserDto userDto, String title, String body, PublicStatus publicStatus, Boolean isDeleted, List<ArticleMediaDto> articleMedias, List<ArticleCommentDto> articleComments, List<ArticleReactionDto> articleReactions, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id
        this.userDto = userDto
        this.title = title
        this.body = body
        this.publicStatus = publicStatus
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
        this.articleMedias = articleMedias
        this.articleComments = articleComments
        this.articleReactions = articleReactions
    }


    @Override
    public String toString() {
        return "ArticleDto{" +
                "id=" + id +
                ", userDto=" + userDto +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", publicStatus=" + publicStatus +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", medias=" + articleMedias +
                '}'
    }
}
