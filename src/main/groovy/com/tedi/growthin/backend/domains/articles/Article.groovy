package com.tedi.growthin.backend.domains.articles

import com.tedi.growthin.backend.domains.enums.PublicStatus
import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
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
import org.hibernate.annotations.Where

import java.time.OffsetDateTime

@Entity
@Table(name = "articles")
class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_id_seq_gen")
    @SequenceGenerator(name = "articles_id_seq_gen", sequenceName = "public.articles_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_users")
    User user

    @Column(length = 512)
    String title

    @Column(nullable = false)
    String body

    //one article has many media
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "article")
    @OrderBy(value = "order ASC")
    List<ArticleMedia> articleMedias

    //one article has many comments
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "article")
    @OrderBy(value = "createdAt ASC")
    @BatchSize(size = 10)
    @Where(clause = "is_deleted = false")
    //only get 10 first (not deleted) comments -> not all the comments (there is an endpoint for that)
    List<ArticleComment> articleComments

    //one article has many reactions
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "article")
    @OrderBy(value = "createdAt DESC")
    List<ArticleReaction> articleReactions

    @Enumerated(EnumType.STRING)
    @Column(name = "public_status", columnDefinition = "public.\"PublicStatus\"", nullable = false)
    PublicStatus publicStatus = PublicStatus.PUBLIC

    @Column(nullable = false)
    Boolean isDeleted = false

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime updatedAt

    Article(Long id,
            User user,
            String title,
            String body,
            List<ArticleMedia> articleMedias,
            List<ArticleComment> articleComments = [],
            List<ArticleReaction> articleReactions = [],
            PublicStatus publicStatus = PublicStatus.PUBLIC,
            Boolean isDeleted = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null) {
        this.id = id
        this.user = user
        this.title = title
        this.body = body
        this.articleMedias = articleMedias
        this.articleComments = articleComments
        this.articleReactions = articleReactions
        this.publicStatus = publicStatus
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    Article(User user,
            String title,
            String body,
            List<ArticleMedia> articleMedias,
            List<ArticleComment> articleComments = [],
            List<ArticleReaction> articleReactions = [],
            PublicStatus publicStatus = PublicStatus.PUBLIC,
            Boolean isDeleted = false,
            OffsetDateTime createdAt = null,
            OffsetDateTime updatedAt = null) {
        this.user = user
        this.title = title
        this.body = body
        this.articleMedias = articleMedias
        this.articleComments = articleComments
        this.articleReactions = articleReactions
        this.publicStatus = publicStatus
        this.isDeleted = isDeleted
        this.createdAt = createdAt
        this.updatedAt = updatedAt
    }

    Article() {}

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", userId=" + user?.id +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", articleMedias=" + articleMedias +
                ", articleComments=" + articleComments +
                ", articleReactions=" + articleReactions +
                ", publicStatus=" + publicStatus +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}'
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Article article = (Article) o

        if (articleComments != article.articleComments) return false
        if (articleMedias != article.articleMedias) return false
        if (articleReactions != article.articleReactions) return false
        if (body != article.body) return false
        if (createdAt != article.createdAt) return false
        if (id != article.id) return false
        if (isDeleted != article.isDeleted) return false
        if (publicStatus != article.publicStatus) return false
        if (title != article.title) return false
        if (updatedAt != article.updatedAt) return false
        if (user != article.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (title != null ? title.hashCode() : 0)
        result = 31 * result + (body != null ? body.hashCode() : 0)
        result = 31 * result + (articleMedias != null ? articleMedias.hashCode() : 0)
        result = 31 * result + (articleComments != null ? articleComments.hashCode() : 0)
        result = 31 * result + (articleReactions != null ? articleReactions.hashCode() : 0)
        result = 31 * result + (publicStatus != null ? publicStatus.hashCode() : 0)
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
