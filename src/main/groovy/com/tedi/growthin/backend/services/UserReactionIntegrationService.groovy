package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.services.reactions.ReactionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service

@Service
class UserReactionIntegrationService {

    @Autowired
    ReactionService reactionService

    List<ReactionDto> findAllReactions (Authentication authentication) throws Exception {
        return reactionService.findAll()
    }

}
