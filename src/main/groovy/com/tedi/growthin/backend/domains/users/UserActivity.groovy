package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.jobs.JobAd
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

@Entity
@Table(name = "user_history")
class UserActivity implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_history_id_seq_gen")
    @SequenceGenerator(name = "user_history_id_seq_gen", sequenceName = "public.user_history_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    User user

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_user_action_types", nullable = false)
    UserActionType userActionType

    @Column(name = "is_article", nullable = false)
    Boolean isArticle = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_articles')
    Article article

    @Column(name = "is_comment", nullable = false)
    Boolean isComment = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_comments')
    ArticleComment comment

    @Column(name = "is_connection_request", nullable = false)
    Boolean isConnectionRequest = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_user_connection_requests')
    UserConnectionRequest userConnectionRequest


    @Column(name = "is_job_ad", nullable = false)
    Boolean isJobAd = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_job_ads')
    JobAd jobAd


    @Column(name = "is_comment_reaction", nullable = false)
    Boolean isCommentReaction = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_user_comments_reactions')
    UserArticleCommentReaction commentReaction

    @Column(name = "is_article_reaction", nullable = false)
    Boolean isArticleReaction = false

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'id_user_articles_reactions')
    UserArticleReaction articleReaction

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserActivity that = (UserActivity) o

        if (article != that.article) return false
        if (articleReaction != that.articleReaction) return false
        if (comment != that.comment) return false
        if (commentReaction != that.commentReaction) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (isArticle != that.isArticle) return false
        if (isArticleReaction != that.isArticleReaction) return false
        if (isComment != that.isComment) return false
        if (isCommentReaction != that.isCommentReaction) return false
        if (isConnectionRequest != that.isConnectionRequest) return false
        if (isJobAd != that.isJobAd) return false
        if (jobAd != that.jobAd) return false
        if (user != that.user) return false
        if (userActionType != that.userActionType) return false
        if (userConnectionRequest != that.userConnectionRequest) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (userActionType != null ? userActionType.hashCode() : 0)
        result = 31 * result + (isArticle != null ? isArticle.hashCode() : 0)
        result = 31 * result + (article != null ? article.hashCode() : 0)
        result = 31 * result + (isComment != null ? isComment.hashCode() : 0)
        result = 31 * result + (comment != null ? comment.hashCode() : 0)
        result = 31 * result + (isConnectionRequest != null ? isConnectionRequest.hashCode() : 0)
        result = 31 * result + (userConnectionRequest != null ? userConnectionRequest.hashCode() : 0)
        result = 31 * result + (isJobAd != null ? isJobAd.hashCode() : 0)
        result = 31 * result + (jobAd != null ? jobAd.hashCode() : 0)
        result = 31 * result + (isCommentReaction != null ? isCommentReaction.hashCode() : 0)
        result = 31 * result + (commentReaction != null ? commentReaction.hashCode() : 0)
        result = 31 * result + (isArticleReaction != null ? isArticleReaction.hashCode() : 0)
        result = 31 * result + (articleReaction != null ? articleReaction.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "UserActivity{" +
                "id=" + id +
                ", user=" + user +
                ", userActionType=" + userActionType +
                ", isArticle=" + isArticle +
                ", article=" + article +
                ", isComment=" + isComment +
                ", comment=" + comment +
                ", isConnectionRequest=" + isConnectionRequest +
                ", userConnectionRequest=" + userConnectionRequest +
                ", isJobAd=" + isJobAd +
                ", jobAd=" + jobAd +
                ", isCommentReaction=" + isCommentReaction +
                ", commentReaction=" + commentReaction +
                ", isArticleReaction=" + isArticleReaction +
                ", articleReaction=" + articleReaction +
                ", createdAt=" + createdAt +
                '}';
    }
}
