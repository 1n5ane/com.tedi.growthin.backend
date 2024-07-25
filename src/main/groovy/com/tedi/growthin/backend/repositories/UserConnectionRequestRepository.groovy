package com.tedi.growthin.backend.repositories

import com.tedi.growthin.backend.domains.users.UserConnectionRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserConnectionRequestRepository extends PagingAndSortingRepository<UserConnectionRequest, Long>, CrudRepository<UserConnectionRequest, Long> {

    Page<UserConnectionRequest> findAll(Pageable pageable)

    @Query("SELECT ucr from UserConnectionRequest ucr where ucr.user.id = :userId and ucr.connectedUser.id = :connectedUserId")
    UserConnectionRequest findByUserIdAndConnectedUserId(@Param("userId") Long userId, @Param("connectedUserId") Long connectedUserId)

}
