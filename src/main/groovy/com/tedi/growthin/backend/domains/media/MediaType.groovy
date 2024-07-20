package com.tedi.growthin.backend.domains.media

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "media_types")
class MediaType implements Serializable{

    @Id
    Integer id

    @Column(nullable = false)
    String name


    @Override
    public String toString() {
        return "MediaType{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        MediaType mediaType = (MediaType) o

        if (id != mediaType.id) return false
        if (name != mediaType.name) return false

        return true
    }

    int hashCode() {
        int result
        result = (id != null ? id.hashCode() : 0)
        result = 31 * result + (name != null ? name.hashCode() : 0)
        return result
    }
}
