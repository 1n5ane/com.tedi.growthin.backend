package com.tedi.growthin.backend.domains.reactions

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import org.hibernate.annotations.CreationTimestamp

import java.time.OffsetDateTime

@Entity
@Table(name = "reactions")
class Reaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reactions_id_seq_gen")
    @SequenceGenerator(name = "reactions_id_seq_gen", sequenceName = "public.reactions_id_seq", allocationSize = 1)
    Long id

    @Column(name = '"alias"',nullable = false)
    String alias = ''

    @Column(nullable = false)
    byte[] image

    @Column
    @CreationTimestamp
    @Temporal(value = TemporalType.TIMESTAMP)
    OffsetDateTime createdAt


    @Override
    public String toString() {
        return "Reaction{" +
                "id=" + id +
                ", alias='" + alias + '\'' +
                ", image=" + image ? "[...]" : null +
                ", createdAt=" + createdAt +
                '}';
    }
}
