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

@Entity
@Table(name = "articles_media")
class ArticleMedia implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_media_seq_gen")
    @SequenceGenerator(name = "articles_media_seq_gen", sequenceName = "public.articles_media_seq")
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_articles", nullable = false)
    Article article

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_media", nullable = false)
    Media media

    @Column(nullable = false)
    Integer order = 0

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt
}
