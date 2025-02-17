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


    @Query("select case when (uc.user.id = :userId) then uc.connectedUser.id else uc.user.id end from UserConnection uc where (uc.user.id in :userIdList or uc.connectedUser.id in :userIdList) and (uc.user.id = :userId or uc.connectedUser.id = :userId)")
    List<Long> findAllConnectedUserIdsByUserIdList(@Param("userId") Long userId, @Param("userIdList") List<Long> userIdList)

    @Query("select case when (uc.user.id = :userId) then uc.connectedUser.id else uc.user.id end from UserConnection uc where (uc.user.username in :userUsernameList or uc.connectedUser.username in :userUsernameList) and (uc.user.id = :userId or uc.connectedUser.id = :userId)")
    List<Long> findAllConnectedUserIdsByUserUsernameList(@Param("userId") Long userId, @Param("userUsernameList") List<String> userUsernameList)

    @Query("SELECT uc FROM UserConnection uc where (uc.user.id = :userId and uc.connectedUser.username like :username%)  or (uc.connectedUser.id = :userId and uc.user.username like :username%)")
    Page<UserConnection> findAllByUserIdAndUsernameLike(@Param("userId") Long userId, @Param("username") String username, Pageable pageable)

    //get all user connections (user id in user_ids or connected_user_ids)
    @Query("SELECT uc FROM UserConnection uc where uc.user.id = :userId or uc.connectedUser.id = :userId")
    Page<UserConnection> findAllByUserId(@Param("userId") Long userId, Pageable pageable)

    @Query("SELECT uc FROM UserConnection uc where (uc.user.id = :userId1 and uc.connectedUser.id = :userId2) or (uc.user.id = :userId2 and uc.connectedUser.id = :userId1)")
    Optional<UserConnection> findByUserIds(@Param("userId1") Long userId1, @Param("userId2") Long userId2)

    //check if users are connected
    @Query("SELECT CASE WHEN COUNT(uc) > 0 THEN TRUE ELSE FALSE END FROM UserConnection uc where (uc.user.id = :userId1 and uc.connectedUser.id = :userId2) or (uc.user.id = :userId2 and uc.connectedUser.id = :userId1)")
    Boolean existsUserConnection(@Param("userId1") Long userId1, @Param("userId2") Long userId2)

    @Query("SELECT uc FROM UserConnection uc where uc.user.id in :userIds or uc.connectedUser.id in :userIds")
    List<UserConnection> findAllByUserIds(@Param("userIds") List<Long> userIds)
}