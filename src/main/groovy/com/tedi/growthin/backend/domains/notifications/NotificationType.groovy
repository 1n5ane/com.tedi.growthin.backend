package com.tedi.growthin.backend.domains.notifications

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "notification_types")
class NotificationType implements Serializable {

    @Id
    Integer id

    @Column(nullable = false)
    String name

    NotificationType() {}

    NotificationType(Integer id, String name) {
        this.id = id
        this.name = name
    }

    @Override
    public String toString() {
        return "NotificationType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        NotificationType that = (NotificationType) o

        if (id != that.id) return false
        if (name != that.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }
}
