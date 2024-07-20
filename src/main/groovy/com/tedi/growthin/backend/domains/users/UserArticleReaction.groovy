package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.reactions.Reaction
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

@Entity
@Table(name = "user_articles_reactions", uniqueConstraints = [
        @UniqueConstraint(
                name = "user_id_reaction_id_articles_id_uniqueness",
                columnNames = ["id_articles", "id_users"]
        )
]) // user can have one reaction per article
class UserArticleReaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_articles_reactions_id_seq_gen")
    @SequenceGenerator(name = "user_articles_reactions_id_seq_gen", sequenceName = "public.user_articles_reactions_id_seq")
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articles", nullable = false)
    Article article


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    User user

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reactions", nullable = false)
    Reaction reaction

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt

    @Override
    public String toString() {
        return "UserArticleReaction{" +
                "id=" + id +
                ", article=" + article +
                ", user=" + user +
                ", reaction=" + reaction +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserArticleReaction that = (UserArticleReaction) o

        if (article != that.article) return false
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
