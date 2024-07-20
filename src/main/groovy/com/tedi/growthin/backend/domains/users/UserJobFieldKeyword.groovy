package com.tedi.growthin.backend.domains.users

import com.tedi.growthin.backend.domains.jobs.JobFieldKeyword
import com.tedi.growthin.backend.domains.users.keys.UserJobFieldKey
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId
import jakarta.persistence.Table

@Entity
@Table(name = 'user_job_field_keywords')
class UserJobFieldKeyword implements Serializable{

    @EmbeddedId
    UserJobFieldKey id

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_users")
    User user

    @ManyToOne
    @MapsId("jobFieldKeywordId")
    @JoinColumn(name = "id_job_field_keywords")
    JobFieldKeyword jobFieldKeyword

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserJobFieldKeyword that = (UserJobFieldKeyword) o

        if (id != that.id) return false
        if (jobFieldKeyword != that.jobFieldKeyword) return false
        if (user != that.user) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (user != null ? user.hashCode() : 0)
        result = 31 * result + (jobFieldKeyword != null ? jobFieldKeyword.hashCode() : 0)
        return result
    }

    @Override
    public String toString() {
        return "UserJobFieldKeyword{" +
                "id=" + id +
                ", user=" + user +
                ", jobFieldKeyword=" + jobFieldKeyword +
                '}';
    }
}
