package com.tedi.growthin.backend.repositories.media

import com.tedi.growthin.backend.domains.media.Media
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface MediaRepository extends PagingAndSortingRepository<Media, Long>, CrudRepository<Media, Long> {

    Page<Media> findAll(Pageable pageable)

    @Query("select m from Media m where m.id in :idList")
    List<Media> findAllByIdIn(@Param("idList") List<Long> idList)

    @Query("select m from Media m where m.user.id = :userId")
    Page<Media> findAllByUserId(@Param("userId") Long userId, Pageable pageable)

}