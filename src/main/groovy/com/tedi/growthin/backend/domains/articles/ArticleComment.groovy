package com.tedi.growthin.backend.domains.articles

import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.OrderBy
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.BatchSize
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "comments")
class ArticleComment implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "comments_id_seq_gen")
    @SequenceGenerator(name = "comments_id_seq_gen", sequenceName = "public.comments_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articles", updatable = false, nullable = false)
    Article article

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "comment")
    @OrderBy(value = "createdAt DESC")
    @BatchSize(size = 20)
    //only get 20 most recent comment reactions
    List<ArticleCommentReaction> commentReactions

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_users", updatable = false, nullable = false)
    User user

    @Column(nullable = false)
    String body

    @Column(nullable = false)
    Boolean isDeleted = false

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    ArticleComment() {}

    ArticleComment(Long id, Article article, List<ArticleCommentReaction> commentReactions, User user, String body, Boolean isDeleted, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.article = article
        this.commentReactions = commentReactions
        this.user = user
        this.body = body
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ArticleComment(Article article, List<ArticleCommentReaction> commentReactions, User user, String body, Boolean isDeleted, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.article = article
        this.commentReactions = commentReactions
        this.user = user
        this.body = body
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    @Override
    public String toString() {
        return "ArticleComment{" +
                "id=" + id +
                ", articleId=" + article?.id +
                ", commentReactions=" + commentReactions +
                ", user=" + user +
                ", body='" + body + '\'' +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ArticleComment that = (ArticleComment) o

        if (article?.id != that.article?.id) return false
        if (body != that.body) return false
        if (commentReactions != that.commentReactions) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (isDeleted != that.isDeleted) return false
        if (updatedAt != that.updatedAt) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (article != null ? article.hashCode() : 0)
        result = 31 * result + (commentReactions != null ? commentReactions.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (body != null ? body.hashCode() : 0)
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
