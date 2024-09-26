package com.tedi.growthin.backend.domains.users.keys

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class UserJobFieldKey implements Serializable {
    @Column(name = "id_users", nullable = false)
    Long userId

    @Column(name = "id_job_field_keywords", nullable = false)
    Long jobFieldKeywordId

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserJobFieldKey that = (UserJobFieldKey) o

        if (jobFieldKeywordId != that.jobFieldKeywordId) return false
        if (userId != that.userId) return false

        return true
    }

    int hashCode() {
        int result
        result = (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (jobFieldKeywordId != null ? jobFieldKeywordId.hashCode() : 0)
        return result
    }
}
