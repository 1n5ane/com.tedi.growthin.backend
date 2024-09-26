package com.tedi.growthin.backend.repositories.reactions

import com.tedi.growthin.backend.domains.reactions.Reaction
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ReactionRepository extends CrudRepository<Reaction, Long> {

    @Query("select r from Reaction r where r.alias = :alias")
    Optional<Reaction> findByAlias(@Param("alias") String alias)
}