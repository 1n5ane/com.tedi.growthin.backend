package com.tedi.growthin.backend.domains.users

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_action_types")
class UserActionType implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id

    @Column(nullable = false, length = 25)
    String type


    @Override
    public String toString() {
        return "UserActionType{" +
                "id=" + id +
                ", type='" + type + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        UserActionType that = (UserActionType) o

        if (id != that.id) return false
        if (type != that.type) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (type != null ? type.hashCode() : 0)
        return result
    }
}
