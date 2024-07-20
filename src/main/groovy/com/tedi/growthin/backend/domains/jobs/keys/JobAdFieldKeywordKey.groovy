package com.tedi.growthin.backend.domains.jobs.keys

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class JobAdFieldKeywordKey implements Serializable{

    @Column(name = "id_job_field_keywords")
    Long jobFieldKeywordId

    @Column(name = "id_job_ads")
    Long jobAdId


    @Override
    public String toString() {
        return "JobAdFieldKeywordKey{" +
                "jobFieldKeywordId=" + jobFieldKeywordId +
                ", jobAdId=" + jobAdId +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        JobAdFieldKeywordKey that = (JobAdFieldKeywordKey) o

        if (jobAdId != that.jobAdId) return false
        if (jobFieldKeywordId != that.jobFieldKeywordId) return false

        return true
    }

    int hashCode() {
        int result
        result = (jobFieldKeywordId != null ? jobFieldKeywordId.hashCode() : 0)
        result = 31 * result + (jobAdId != null ? jobAdId.hashCode() : 0)
        return result
    }
}
