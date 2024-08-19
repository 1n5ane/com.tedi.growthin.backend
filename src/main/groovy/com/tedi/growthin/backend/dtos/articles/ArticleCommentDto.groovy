package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleCommentDto implements Serializable {

    def id
    def articleId
    @JsonProperty(value = "user")
    UserDto userDto
    List<ArticleCommentReactionDto> commentReactions
    String comment
    Boolean isDeleted
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    ArticleCommentDto() {
        this.commentReactions = []
    }

    ArticleCommentDto(id, articleId, UserDto userDto, List<ArticleCommentReactionDto> commentReactions, String comment, Boolean isDeleted, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.articleId = articleId
        this.userDto = userDto
        this.commentReactions = commentReactions
        this.comment = comment
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    @Override
    public String toString() {
        return "ArticleCommentDto{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userDto=" + userDto +
                ", commentReactions=" + commentReactions +
                ", comment='" + comment + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
