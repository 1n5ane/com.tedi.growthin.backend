package com.tedi.growthin.backend.domains.jobs

import com.tedi.growthin.backend.domains.jobs.keys.JobAdApplicantKey
import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(name = "job_ads_applicants", uniqueConstraints = [
        @UniqueConstraint(
                name = "id_job_ads",
                columnNames = ["id_job_ads", "id_users"]
        )
])
class JobAdApplicant implements Serializable{

    @EmbeddedId
    JobAdApplicantKey id

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("jodAdId")
    @JoinColumn(name = "id_job_ads")
    JobAd jobAd

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "id_users")
    User user

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = 'cv_media_id')
    Media cvMedia


    @Override
    public String toString() {
        return "JobAdApplicant{" +
                "id=" + id +
                ", jobAd=" + jobAd +
                ", user=" + user +
                ", cvMedia=" + cvMedia +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        JobAdApplicant that = (JobAdApplicant) o

        if (cvMedia != that.cvMedia) return false
        if (id != that.id) return false
        if (jobAd != that.jobAd) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (jobAd != null ? jobAd.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (cvMedia != null ? cvMedia.hashCode() : 0)
        return result
    }
}
