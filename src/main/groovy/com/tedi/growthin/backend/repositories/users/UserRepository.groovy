package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.users.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository extends PagingAndSortingRepository<User, Long>, CrudRepository<User, Long> {

    Optional<User> findById(Long id)

    Optional<User> findByUsernameOrEmail(String username, String email)

    User findByUsername(String username)

    User findByEmail(String email)

    Boolean existsByUsername(String username)

    Boolean existsByEmail(String email)

    Page<User> findAll(Pageable pageable)

    @Query("SELECT u FROM User u where u.locked = :locked")
    Page<User> findAllByLocked(@Param("locked") Boolean locked, Pageable pageable)

}