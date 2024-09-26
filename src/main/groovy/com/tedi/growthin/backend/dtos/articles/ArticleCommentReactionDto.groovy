package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleCommentReactionDto {

    def id
    @JsonProperty(value = "user")
    UserDto userDto
    def articleId
    def commentId
    @JsonProperty(value = "reaction")
    ReactionDto reactionDto
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    ArticleCommentReactionDto() {}

    ArticleCommentReactionDto(id, UserDto userDto, commentId, ReactionDto reactionDto, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.userDto = userDto
        this.commentId = commentId
        this.reactionDto = reactionDto
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ArticleCommentReactionDto(id, UserDto userDto, articleId, commentId, ReactionDto reactionDto, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.userDto = userDto
        this.articleId = articleId
        this.commentId = commentId
        this.reactionDto = reactionDto
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }


    @Override
    public String toString() {
        return "ArticleCommentReactionDto{" +
                "id=" + id +
                ", userDto=" + userDto +
                ", commentId=" + commentId +
                ", reactionDto=" + reactionDto +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
