package com.tedi.growthin.backend.domains.jobs.keys

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class JobAdApplicantKey implements Serializable {
    @Column(name = "id_job_ads", nullable = false)
    Long jodAdId

    @Column(name = "id_users", nullable = false)
    Long userId


    @Override
    public String toString() {
        return "JobAdApplicantKey{" +
                "jodAdId=" + jodAdId +
                ", userId=" + userId +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        JobAdApplicantKey that = (JobAdApplicantKey) o

        if (jodAdId != that.jodAdId) return false
        if (userId != that.userId) return false

        return true
    }

    int hashCode() {
        int result
        result = (jodAdId != null ? jodAdId.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        return result
    }
}
