package com.tedi.growthin.backend.domains.articles

import com.tedi.growthin.backend.domains.media.Media
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
@Table(name = "articles_media")
class ArticleMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_media_seq_gen")
    @SequenceGenerator(name = "articles_media_seq_gen", sequenceName = "public.articles_media_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articles", updatable = false, nullable = false)
    Article article

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_media", nullable = false)
    Media media

    @Column(name = '"order"', nullable = false)
    Integer order = 0

    @Column(updatable = false)
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt

    ArticleMedia() {}

    ArticleMedia(Long id, Article article, Media media, Integer order, OffsetDateTime createdAt = null) {
        this.id = id
        this.article = article
        this.media = media
        this.order = order
        this.createdAt = createdAt
    }

    ArticleMedia(Article article, Media media, Integer order, OffsetDateTime createdAt = null) {
        this.article = article
        this.media = media
        this.order = order
        this.createdAt = createdAt
    }

    @Override
    public String toString() {
        return "ArticleMedia{" +
                "id=" + id +
                ", articleId=" + article?.id +
                ", media=" + media +
                ", order=" + order +
                ", createdAt=" + createdAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ArticleMedia that = (ArticleMedia) o

        if (article?.id != that.article?.id) return false
        if (createdAt != that.createdAt) return false
        if (id != that.id) return false
        if (media != that.media) return false
        if (order != that.order) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (article != null ? article.hashCode() : 0)
        result = 31 * result + (media != null ? media.hashCode() : 0)
        result = 31 * result + (order != null ? order.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        return result
    }
}
