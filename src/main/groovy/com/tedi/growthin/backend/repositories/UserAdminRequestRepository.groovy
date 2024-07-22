package com.tedi.growthin.backend.repositories

import com.tedi.growthin.backend.domains.users.UserAdminRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param

interface UserAdminRequestRepository extends PagingAndSortingRepository<UserAdminRequest, Long>, CrudRepository<UserAdminRequest, Long> {
    Page<UserAdminRequest> findAll(Pageable pageable)

    Optional<UserAdminRequest> findById(Long id)

    @Query(value = "SELECT uar FROM UserAdminRequest uar where uar.user.id=:userId")
    UserAdminRequest findByUserId(@Param("userId") Long userId)
}
