package com.tedi.growthin.backend.services.reactions

import com.tedi.growthin.backend.domains.reactions.Reaction
import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.repositories.reactions.ReactionRepository
import com.tedi.growthin.backend.utils.exception.validation.reactions.ReactionException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ReactionService {

    @Autowired
    ReactionRepository reactionRepository

    ReactionDto findReaction(ReactionDto reactionDto) throws Exception {
        if (reactionDto == null) {
            throw new ReactionException("Reaction can't be empty")
        }

        if (reactionDto.alias != null && !reactionDto.alias.isEmpty()) {
            return this.findByAlias(reactionDto.alias)
        } else if (reactionDto.id != null) {
            return this.findById((Long) reactionDto.id)
        }

        //empty id and alias provided
        throw new ReactionException("No alias or id provided")
    }

    ReactionDto findByAlias(String alias) throws Exception {
        if (alias == null || alias.isEmpty())
            throw new ReactionException("Reaction alias can't be empty")

        Optional<Reaction> optionalReaction = reactionRepository.findByAlias(alias)

        if (optionalReaction.isEmpty())
            return null

        return reactionDtoFromReaction(optionalReaction.get())
    }

    ReactionDto findById(Long id) throws Exception {
        if (id == null)
            throw new ReactionException("Reaction id can't be empty")

        Optional<Reaction> optionalReaction = reactionRepository.findById(id)

        if (optionalReaction.isEmpty())
            return null

        return reactionDtoFromReaction(optionalReaction.get())
    }

    static ReactionDto reactionDtoFromReaction(Reaction reaction) {
        return new ReactionDto(reaction.id, reaction.alias, reaction.image, reaction.createdAt)
    }

}
