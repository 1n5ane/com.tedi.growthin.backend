package com.tedi.growthin.backend.domains.jobs

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
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

@Entity
@Table(name = "job_ads")
class JobAd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_ads_id_seq_gen")
    @SequenceGenerator(name = "job_ads_id_seq_gen", sequenceName = "public.job_ads_id_seq")
    Long id

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_users", nullable = false)
    User user

    @Column(nullable = false)
    String title

    @Column(nullable = false)
    String description

    @Column(nullable = false)
    Boolean isActive = true

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "public_status")
    PublicStatus publicStatus = PublicStatus.PUBLIC

    @Column
    Boolean isDeleted = false

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    Date createdAt


    @Column
    @Temporal(value = TemporalType.TIMESTAMP)
    Date updatedAt


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "jobAd")
    List<JobAdApplicant> jobAdApplicantList


    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        JobAd jobAd = (JobAd) o

        if (createdAt != jobAd.createdAt) return false
        if (description != jobAd.description) return false
        if (id != jobAd.id) return false
        if (isActive != jobAd.isActive) return false
        if (isDeleted != jobAd.isDeleted) return false
        if (jobAdApplicantList != jobAd.jobAdApplicantList) return false
        if (publicStatus != jobAd.publicStatus) return false
        if (title != jobAd.title) return false
        if (updatedAt != jobAd.updatedAt) return false
        if (user != jobAd.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (title != null ? title.hashCode() : 0)
        result = 31 * result + (description != null ? description.hashCode() : 0)
        result = 31 * result + (isActive != null ? isActive.hashCode() : 0)
        result = 31 * result + (publicStatus != null ? publicStatus.hashCode() : 0)
        result = 31 * result + (isDeleted != null ? isDeleted.hashCode() : 0)
        result = 31 * result + (createdAt != null ? createdAt.hashCode() : 0)
        result = 31 * result + (updatedAt != null ? updatedAt.hashCode() : 0)
        result = 31 * result + (jobAdApplicantList != null ? jobAdApplicantList.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "JobAd{" +
                "id=" + id +
                ", user=" + user +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", publicStatus=" + publicStatus +
                ", isDeleted=" + isDeleted +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", jobAdApplicantList=" + jobAdApplicantList +
                '}';
    }
}
