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
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "articles")
class Article implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "articles_id_seq_gen")
    @SequenceGenerator(name = "articles_id_seq_gen", sequenceName = "public.articles_id_seq", allocationSize = 1)
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users")
    User user

    @Column(length = 512)
    String title

    @Column(nullable = false)
    String body

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "public_status")
    PublicStatus publicStatus = PublicStatus.PUBLIC

    @Column(nullable = false)
    Boolean isDeleted = false

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt

    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt


    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", publicStatus=" + publicStatus +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        Article article = (Article) o

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
        result = 31 * result + (publicStatus != null ? publicStatus.hashCode() : 0)
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        return result
    }
}
