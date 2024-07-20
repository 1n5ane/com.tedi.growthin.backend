package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.articles.ArticleComment
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
@Table(name = "user_comments_reactions", uniqueConstraints = [
        @UniqueConstraint(
                name = "users_id_reactions_id_comments_id_uq",
                columnNames = ["id_users", "id_comments"]
        )
])
class UserArticleCommentReaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_comments_reactions_id_seq_gen")
    @SequenceGenerator(name = "user_comments_reactions_id_seq_gen", sequenceName = "public.user_comments_reactions_id_seq")
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_comments", nullable = false)
    ArticleComment comment


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

}
