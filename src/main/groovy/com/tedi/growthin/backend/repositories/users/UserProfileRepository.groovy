package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.users.UserProfile
import org.hibernate.query.QueryParameter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, Long>, CrudRepository<UserProfile, Long> {

    @Query("select up from UserProfile up where up.user.username = :username")
    Optional<UserProfile> findByUsername(@Param("username") String username)

    @Query("select up from UserProfile up where up.profileId in :ids")
    List<UserProfile> findAllByIdsIn(@Param("ids") List<Long> ids)

    @Query("select up from UserProfile up where up.user.username in :usernames")
    List<UserProfile> findAllByUsernamesIn(@Param("usernames") List<String> usernames)

    @Query("select up from UserProfile up where up.user.username like %:username%")
    Page<UserProfile> findAllByUsernameLike(@Param("username") String username, Pageable pageable)
}