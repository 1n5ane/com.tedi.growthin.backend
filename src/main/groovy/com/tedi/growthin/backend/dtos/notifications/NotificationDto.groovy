package com.tedi.growthin.backend.dtos.notifications

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.dtos.chats.ChatRoomDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionRequestDto
import com.tedi.growthin.backend.dtos.users.UserDto

import java.time.OffsetDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class NotificationDto implements Serializable {

    def id

    @JsonProperty("user")
    UserDto userDto

    @JsonProperty("recipient")
    UserDto recipientDto

    @JsonProperty("notificationType")
    NotificationTypeDto notificationTypeDto

    Boolean viewed = false

    //articles
    Boolean isArticle
    @JsonProperty("article")
    ArticleDto articleDto

    Boolean isArticleReaction
    @JsonProperty("articleReaction")
    ArticleReactionDto articleReactionDto
    //end articles

    //comments
    Boolean isComment
    @JsonProperty("articleComment")
    ArticleCommentDto articleCommentDto

    Boolean isCommentReaction
    @JsonProperty("articleCommentReaction")
    ArticleCommentReactionDto articleCommentReactionDto
    //end comments

    //connection requests
    Boolean isConnectionRequest
    @JsonProperty("userConnectionRequest")
    UserConnectionRequestDto userConnectionRequestDto
    //end connection requests

    //TODO: add job ads

    //chat
    Boolean isChat
    @JsonProperty("chatRoom")
    ChatRoomDto chatRoomDto
    //end chat

    OffsetDateTime createdAt

    NotificationDto() {}

    //Article notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isArticle, ArticleDto articleDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isArticle = isArticle
        this.articleDto = articleDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isConnectionRequest = false
        this.isCommentReaction = false
        this.isComment = false
        this.isArticleReaction = false
        this.isChat = false
    }

    //Article reaction notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isArticleReaction, ArticleReactionDto articleReactionDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isArticleReaction = isArticleReaction
        this.articleReactionDto = articleReactionDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isConnectionRequest = false
        this.isCommentReaction = false
        this.isComment = false
        this.isArticle = false
        this.isChat = false
    }

    //comment notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isComment, ArticleCommentDto articleCommentDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isComment = isComment
        this.articleCommentDto = articleCommentDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isConnectionRequest = false
        this.isCommentReaction = false
        this.isArticle = false
        this.isArticleReaction = false
        this.isChat = false
    }

    //comment reaction notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isCommentReaction, ArticleCommentReactionDto articleCommentReactionDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isCommentReaction = isCommentReaction
        this.articleCommentReactionDto = articleCommentReactionDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isConnectionRequest = false
        this.isComment = false
        this.isArticle = false
        this.isArticleReaction = false
        this.isChat = false
    }

    //connection request notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isConnectionRequest, UserConnectionRequestDto userConnectionRequestDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isConnectionRequest = isConnectionRequest
        this.userConnectionRequestDto = userConnectionRequestDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isComment = false
        this.isCommentReaction = false
        this.isArticle = false
        this.isArticleReaction = false
        this.isChat = false
    }

    //chat notification
    NotificationDto(id, UserDto userDto, UserDto recipientDto, NotificationTypeDto notificationTypeDto, Boolean isChat, ChatRoomDto chatRoomDto, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.userDto = userDto
        this.recipientDto = recipientDto
        this.notificationTypeDto = notificationTypeDto
        this.isChat = isChat
        this.chatRoomDto = chatRoomDto
        this.viewed = viewed
        this.createdAt = createdAt

        this.isConnectionRequest = false
        this.isCommentReaction = false
        this.isArticle = false
        this.isArticleReaction = false
        this.isComment = false
    }


    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", userDto=" + userDto +
                ", recipientDto=" + recipientDto +
                ", notificationTypeDto=" + notificationTypeDto +
                ", viewed=" + viewed +
                ", isArticle=" + isArticle +
                ", articleDto=" + articleDto +
                ", isArticleReaction=" + isArticleReaction +
                ", articleReactionDto=" + articleReactionDto +
                ", isComment=" + isComment +
                ", articleCommentDto=" + articleCommentDto +
                ", isCommentReaction=" + isCommentReaction +
                ", articleCommentReactionDto=" + articleCommentReactionDto +
                ", isConnectionRequest=" + isConnectionRequest +
                ", userConnectionRequestDto=" + userConnectionRequestDto +
                ", isChat=" + isChat +
                ", chatRoomDto=" + chatRoomDto +
                ", createdAt=" + createdAt +
                '}';
    }


}
