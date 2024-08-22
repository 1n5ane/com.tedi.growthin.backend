package com.tedi.growthin.backend.domains.notifications

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.articles.ArticleCommentReaction
import com.tedi.growthin.backend.domains.articles.ArticleReaction
import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.jobs.JobAd
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "notifications")
class Notification implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_id_seq_gen")
    @SequenceGenerator(name = "notification_id_seq_gen", sequenceName = "public.notification_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    User user

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recipient_id")
    User recipient

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "notification_type_id")
    NotificationType notificationType

    @Column(name = "viewed", nullable = false)
    Boolean viewed = false


    @Column(name = "is_article", nullable = false)
    Boolean isArticle = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_articles", updatable = false)
    Article article = null


    @Column(name = "is_article_reaction", nullable = false)
    Boolean isArticleReaction = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_articles_reactions", updatable = false)
    ArticleReaction articleReaction = null


    @Column(name = "is_comment", nullable = false)
    Boolean isComment = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_comments", updatable = false)
    ArticleComment comment = null


    @Column(name = "is_comment_reaction", nullable = false)
    Boolean isCommentReaction = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_comments_reactions", updatable = false)
    ArticleCommentReaction commentReaction = null


    @Column(name = "is_connection_request", nullable = false)
    Boolean isConnectionRequest = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_connection_requests", updatable = false)
    UserConnectionRequest connectionRequest = null


    @Column(name = "is_job_ad", nullable = false)
    Boolean isJobAd = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_job_ads", updatable = false)
    JobAd jobAd = null


    @Column(name = "is_chat", nullable = false)
    Boolean isChat = false

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_chat_rooms", updatable = false)
    ChatRoom chatRoom = null


    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    Notification() {}

    //ARTICLE NOTIFICATION
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isArticle, Article article, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isArticle = isArticle
        this.article = article
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(User user, User recipient, NotificationType notificationType, Boolean isArticle, Article article, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isArticle = isArticle
        this.article = article
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isArticleReaction, ArticleReaction articleReaction, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isArticleReaction = isArticleReaction
        this.articleReaction = articleReaction
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(User user, User recipient, NotificationType notificationType, Boolean isArticleReaction, ArticleReaction articleReaction, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isArticleReaction = isArticleReaction
        this.articleReaction = articleReaction
        this.viewed = viewed
        this.createdAt = createdAt
    }


    //COMMENT NOTIFICATION
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isComment, ArticleComment comment, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isComment = isComment
        this.comment = comment
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(User user, User recipient, NotificationType notificationType, Boolean isComment, ArticleComment comment, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isComment = isComment
        this.comment = comment
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isCommentReaction, ArticleCommentReaction commentReaction, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isCommentReaction = isCommentReaction
        this.commentReaction = commentReaction
        this.viewed = viewed
        this.createdAt = createdAt
    }
    Notification(User user, User recipient, NotificationType notificationType, Boolean isCommentReaction, ArticleCommentReaction commentReaction, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isCommentReaction = isCommentReaction
        this.commentReaction = commentReaction
        this.viewed = viewed
        this.createdAt = createdAt
    }


    //CONNECTION REQUEST NOTIFICATION
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isConnectionRequest, UserConnectionRequest connectionRequest, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isConnectionRequest = isConnectionRequest
        this.connectionRequest = connectionRequest
        this.viewed = viewed
        this.createdAt = createdAt
    }

    Notification(User user, User recipient, NotificationType notificationType, Boolean isConnectionRequest, UserConnectionRequest connectionRequest, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isConnectionRequest = isConnectionRequest
        this.connectionRequest = connectionRequest
        this.viewed = viewed
        this.createdAt = createdAt
    }

    //TODO: jobAd constructors (job ads are not yet implemented in app)


    //CHAT NOTIFICATION
    Notification(Long id, User user, User recipient, NotificationType notificationType, Boolean isChat, ChatRoom chatRoom, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isChat = isChat
        this.chatRoom = chatRoom
        this.viewed = viewed
        this.createdAt = createdAt
    }

    Notification(User user, User recipient, NotificationType notificationType, Boolean isChat, ChatRoom chatRoom, Boolean viewed = false, OffsetDateTime createdAt = null) {
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.isChat = isChat
        this.chatRoom = chatRoom
        this.viewed = viewed
        this.createdAt = createdAt
    }


    //all fields constructor
    Notification(Long id,
                 User user,
                 User recipient,
                 NotificationType notificationType,
                 Boolean viewed,
                 Boolean isArticle,
                 Boolean isArticleReaction,
                 Article article,
                 Boolean isComment,
                 Boolean isCommentReaction,
                 ArticleComment comment,
                 Boolean isConnectionRequest,
                 UserConnectionRequest connectionRequest,
                 Boolean isJobAd,
                 JobAd jobAd,
                 Boolean isChat,
                 ChatRoom chatRoom,
                 OffsetDateTime createdAt = null) {
        this.id = id
        this.user = user
        this.recipient = recipient
        this.notificationType = notificationType
        this.viewed = viewed
        this.isArticle = isArticle
        this.isArticleReaction = isArticleReaction
        this.article = article
        this.isComment = isComment
        this.isCommentReaction = isCommentReaction
        this.comment = comment
        this.isConnectionRequest = isConnectionRequest
        this.connectionRequest = connectionRequest
        this.isJobAd = isJobAd
        this.jobAd = jobAd
        this.isChat = isChat
        this.chatRoom = chatRoom
        this.createdAt = createdAt
    }


    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Notification that = (Notification) o

        if (article != that.article) return false
        if (chatRoom != that.chatRoom) return false
        if (comment != that.comment) return false
        if (connectionRequest != that.connectionRequest) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (isArticle != that.isArticle) return false
        if (isArticleReaction != that.isArticleReaction) return false
        if (isChat != that.isChat) return false
        if (isComment != that.isComment) return false
        if (isCommentReaction != that.isCommentReaction) return false
        if (isConnectionRequest != that.isConnectionRequest) return false
        if (isJobAd != that.isJobAd) return false
        if (jobAd != that.jobAd) return false
        if (notificationType != that.notificationType) return false
        if (recipient != that.recipient) return false
        if (user != that.user) return false
        if (viewed != that.viewed) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (recipient != null ? recipient.hashCode() : 0)
        result = 31 * result + (notificationType != null ? notificationType.hashCode() : 0)
        result = 31 * result + (viewed != null ? viewed.hashCode() : 0)
        result = 31 * result + (isArticle != null ? isArticle.hashCode() : 0)
        result = 31 * result + (isArticleReaction != null ? isArticleReaction.hashCode() : 0)
        result = 31 * result + (article != null ? article.hashCode() : 0)
        result = 31 * result + (isComment != null ? isComment.hashCode() : 0)
        result = 31 * result + (isCommentReaction != null ? isCommentReaction.hashCode() : 0)
        result = 31 * result + (comment != null ? comment.hashCode() : 0)
        result = 31 * result + (isConnectionRequest != null ? isConnectionRequest.hashCode() : 0)
        result = 31 * result + (connectionRequest != null ? connectionRequest.hashCode() : 0)
        result = 31 * result + (isJobAd != null ? isJobAd.hashCode() : 0)
        result = 31 * result + (jobAd != null ? jobAd.hashCode() : 0)
        result = 31 * result + (isChat != null ? isChat.hashCode() : 0)
        result = 31 * result + (chatRoom != null ? chatRoom.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", user=" + user +
                ", recipient=" + recipient +
                ", notificationType=" + notificationType +
                ", viewed=" + viewed +
                ", isArticle=" + isArticle +
                ", isArticleReaction=" + isArticleReaction +
                ", article=" + article +
                ", isComment=" + isComment +
                ", isCommentReaction=" + isCommentReaction +
                ", comment=" + comment +
                ", isConnectionRequest=" + isConnectionRequest +
                ", connectionRequest=" + connectionRequest +
                ", isJobAd=" + isJobAd +
                ", jobAd=" + jobAd +
                ", isChat=" + isChat +
                ", chatRoom=" + chatRoom +
                ", createdAt=" + createdAt +
                '}';
    }
}
