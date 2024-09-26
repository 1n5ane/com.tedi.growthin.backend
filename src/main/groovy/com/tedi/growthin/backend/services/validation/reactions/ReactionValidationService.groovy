package com.tedi.growthin.backend.services.validation.reactions

import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.reactions.ReactionException
import org.springframework.stereotype.Service

@Service
class ReactionValidationService implements ValidationService {

    @Override
    void validate(def reactionDto) throws ValidationException {
        if (reactionDto == null) {
            throw new ReactionException("Reaction can't be empty")
        }

        reactionDto = reactionDto as ReactionDto

        if (reactionDto.id == null && reactionDto.alias == null) {
            throw new ReactionException("No id or alias provided for reaction")
        }
    }
}
