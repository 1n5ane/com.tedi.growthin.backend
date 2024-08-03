package com.tedi.growthin.backend.repositories.users

import com.tedi.growthin.backend.domains.users.UserProfile
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface UserProfileRepository extends PagingAndSortingRepository<UserProfile, Long>, CrudRepository<UserProfile, Long> {
    
}