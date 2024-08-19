package com.tedi.growthin.backend.domains.articles


import com.tedi.growthin.backend.domains.reactions.Reaction
import com.tedi.growthin.backend.domains.users.User
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
import jakarta.persistence.UniqueConstraint
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "user_comments_reactions", uniqueConstraints = [
        @UniqueConstraint(
                name = "users_id_reactions_id_comments_id_uq",
                columnNames = ["id_users", "id_comments"]
        )
])
class ArticleCommentReaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_comments_reactions_id_seq_gen")
    @SequenceGenerator(name = "user_comments_reactions_id_seq_gen", sequenceName = "public.user_comments_reactions_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comments", updatable = false, nullable = false)
    ArticleComment comment


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", updatable = false, nullable = false)
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reactions", nullable = false)
    Reaction reaction

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    ArticleCommentReaction() {}

    ArticleCommentReaction(Long id, ArticleComment comment, User user, Reaction reaction, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.comment = comment
        this.user = user
        this.reaction = reaction
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ArticleCommentReaction(ArticleComment comment, User user, Reaction reaction, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.comment = comment
        this.user = user
        this.reaction = reaction
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    @Override
    public String toString() {
        return "ArticleCommentReaction{" +
                "id=" + id +
                ", comment=" + comment +
                ", userId=" + user?.id +
                ", reaction=" + reaction +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ArticleCommentReaction that = (ArticleCommentReaction) o

        if (comment != that.comment) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (reaction != that.reaction) return false
        if (updatedAt != that.updatedAt) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (comment != null ? comment.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (reaction != null ? reaction.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
