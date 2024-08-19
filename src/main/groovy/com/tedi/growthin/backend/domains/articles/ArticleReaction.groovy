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
@Table(name = "user_articles_reactions", uniqueConstraints = [
        @UniqueConstraint(
                name = "user_id_reaction_id_articles_id_uniqueness",
                columnNames = ["id_articles", "id_users"]
        )
])
// user can have one reaction per article
class ArticleReaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_articles_reactions_id_seq_gen")
    @SequenceGenerator(name = "user_articles_reactions_id_seq_gen", sequenceName = "public.user_articles_reactions_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articles", updatable = false, nullable = false)
    Article article


    @ManyToOne(fetch = FetchType.EAGER)
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

    ArticleReaction() {}

    ArticleReaction(Long id, Article article, User user, Reaction reaction, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.id = id
        this.article = article
        this.user = user
        this.reaction = reaction
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    ArticleReaction(Article article, User user, Reaction reaction, OffsetDateTime createdAt = null, OffsetDateTime updatedAt = null) {
        this.article = article
        this.user = user
        this.reaction = reaction
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    @Override
    public String toString() {
        return "UserArticleReaction{" +
                "id=" + id +
                ", articleId=" + article?.id +
                ", user=" + user +
                ", reaction=" + reaction +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ArticleReaction that = (ArticleReaction) o

        if (article?.id != that.article?.id) return false
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
        result = 31 * result + (article != null ? article.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (reaction != null ? reaction.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
