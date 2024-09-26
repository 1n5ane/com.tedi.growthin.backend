package com.tedi.growthin.backend.repositories.media

import com.tedi.growthin.backend.domains.media.MediaType
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MediaTypeRepository extends CrudRepository<MediaType, Integer> {

    @Query("select mt from MediaType mt where mt.name like %:name%")
    MediaType findByNameContaining(@Param("name") String name)
}
