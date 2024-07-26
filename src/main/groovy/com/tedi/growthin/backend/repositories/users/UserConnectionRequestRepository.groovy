package com.tedi.growthin.backend.repositories.users

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

    @Query("select ucr from UserConnectionRequest ucr where ucr.user.id = :userId and ucr.connectedUser.id = :connectedUserId")
    UserConnectionRequest findByUserIdAndConnectedUserId(@Param("userId") Long userId, @Param("connectedUserId") Long connectedUserId)

    //it doesn't matter who made the request
    @Query("select ucr from UserConnectionRequest ucr where (ucr.user.id = :userId1 and ucr.connectedUser.id = :userId2) or (ucr.user.id = :userId2 and ucr.connectedUser.id = :userId1) ")
    Optional<UserConnectionRequest> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2)
}
