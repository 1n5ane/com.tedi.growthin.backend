package com.tedi.growthin.backend.dtos.articles

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class ArticleReactionDto implements Serializable{

    def id
    def articleId
    @JsonProperty(value = "user")
    UserDto userDto
    @JsonProperty(value = "reaction")
    ReactionDto reactionDto
    OffsetDateTime createdAt
    OffsetDateTime updatedAt

    ArticleReactionDto() {}

    ArticleReactionDto(id, articleId, UserDto userDto, ReactionDto reactionDto, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.articleId = articleId
        this.userDto = userDto
        this.reactionDto = reactionDto
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }


    @Override
    public String toString() {
        return "ArticleReactionDto{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userDto=" + userDto +
                ", reactionDto=" + reactionDto +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
