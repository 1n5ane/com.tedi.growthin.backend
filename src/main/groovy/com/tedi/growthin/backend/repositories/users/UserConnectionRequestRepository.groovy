package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.enums.UserConnectionRequestStatus
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


    @Query("SELECT count(1) FROM UserConnectionRequest ucr where ucr.connectedUser.id = :userId and CAST(ucr.status AS string) = :status")
    //count all requests by status made TO userId
    Long countAllToUserByUserIdAndStatus(@Param("userId")Long userId, @Param("status") String status)

    //find all requests by status made TO userId
    @Query("SELECT ucr FROM UserConnectionRequest ucr where ucr.connectedUser.id = :userId and CAST(ucr.status AS string) = :status")
    Page<UserConnectionRequest> findAllToUserByUserIdAndStatus(@Param("userId")Long userId, @Param("status") String status, Pageable pageable)

    //count all requests by status made BY userId
    @Query("SELECT count(1) FROM UserConnectionRequest ucr where ucr.user.id = :userId and CAST(ucr.status AS string) = :status")
    Long countAllFromUserByUserIdAndStatus(@Param("userId")Long userId, @Param("status") String status)

    //find all requests by status made BY userId
    @Query("SELECT ucr FROM UserConnectionRequest ucr where ucr.user.id = :userId and CAST(ucr.status AS string) = :status")
    Page<UserConnectionRequest> findAllFromUserByUserIdAndStatus(@Param("userId")Long userId, @Param("status") String status, Pageable pageable)

}
