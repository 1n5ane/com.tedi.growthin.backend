package com.tedi.growthin.backend.domains.jobs

import com.tedi.growthin.backend.domains.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany
import jakarta.persistence.SequenceGenerator
import jakarta.persistence.Table

@Entity
@Table(name = "job_field_keywords")
class JobFieldKeyword implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "job_field_keywords_id_seq_gen")
    @SequenceGenerator(name = "job_field_keywords_id_seq_gen", sequenceName = "public.job_field_keywords_id_seq", allocationSize = 1)
    Long id

    @Column(nullable = false, length = 255)
    String name

//    get users having that keyword? and ads that contain this keyword
//    @ManyToMany
//    Set<User> usersJobFieldKeyword
}
