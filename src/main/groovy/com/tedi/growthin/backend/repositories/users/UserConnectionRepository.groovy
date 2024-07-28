package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.users.UserConnection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserConnectionRepository extends PagingAndSortingRepository<UserConnection, Long>, CrudRepository<UserConnection, Long> {

    Page<UserConnection> findAll(Pageable pageable)

    @Query("select case when (uc.user.id = :userId) then uc.connectedUser.id else uc.user.id end from UserConnection uc where uc.user.id = :userId or uc.connectedUser.id = :userId")
    List<Long> findAllConnectedUserIdsByUserId(@Param("userId") Long userId)

    //get all user connections (user id in user_ids or connected_user_ids)
    @Query("SELECT uc FROM UserConnection uc where uc.user.id = :userId or uc.connectedUser.id = :userId")
    Page<UserConnection> findAllByUserId(@Param("userId")Long userId, Pageable pageable)

    @Query("SELECT uc FROM UserConnection uc where (uc.user.id = :userId1 and uc.connectedUser.id = :userId2) or (uc.user.id = :userId2 and uc.connectedUser.id = :userId1)")
    Optional<UserConnection> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2)

    //check if users are connected
    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN TRUE ELSE FALSE END FROM UserConnection uc where (uc.user.id = :userId1 and uc.connectedUser.id = :userId2) or (uc.user.id = :userId2 and uc.connectedUser.id = :userId1)")
    Boolean existsUserConnection(@Param("userId1") Long userId1, @Param("userId2") Long userId2)
}