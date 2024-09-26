package com.tedi.growthin.backend.domains.jobs

import com.tedi.growthin.backend.domains.jobs.keys.JobAdFieldKeywordKey
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = "job_ads_keywords")
class JobAdFieldKeyword {

    @EmbeddedId
    JobAdFieldKeywordKey id

    @ManyToOne
    @MapsId("jobFieldKeywordId")
    @JoinColumn(name = "id_job_field_keywords")
    JobFieldKeyword keyword

    @ManyToOne
    @MapsId("jobAdId")
    @JoinColumn(name = "id_job_ads")
    JobAd jobAd


    @Override
    public String toString() {
        return "JobAdFieldKeyword{" +
                "id=" + id +
                ", keyword=" + keyword +
                ", jobAd=" + jobAd +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        JobAdFieldKeyword that = (JobAdFieldKeyword) o

        if (id != that.id) return false
        if (jobAd != that.jobAd) return false
        if (keyword != that.keyword) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (keyword != null ? keyword.hashCode() : 0)
        result = 31 * result + (jobAd != null ? jobAd.hashCode() : 0)
        return result
    }
}
