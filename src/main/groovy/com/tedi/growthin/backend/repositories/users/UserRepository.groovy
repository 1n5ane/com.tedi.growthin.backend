package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.users.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
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

}