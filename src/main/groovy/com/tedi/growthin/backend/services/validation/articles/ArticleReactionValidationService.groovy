package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.services.validation.reactions.ReactionValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleReactionException
import org.springframework.stereotype.Service

@Service
class ArticleReactionValidationService extends ReactionValidationService {

    @Override
    void validate(def articleReactionDto) throws ValidationException {
        if(articleReactionDto == null){
            throw new ArticleReactionException("Article reaction can't be empty")
        }

        articleReactionDto = articleReactionDto as ArticleReactionDto

        if(articleReactionDto.articleId == null){
            throw new ArticleReactionException("Article id can't be empty")
        }

        if(articleReactionDto.userDto == null || articleReactionDto.userDto.id == null){
            throw new ArticleReactionException("User of article reaction can't be empty")
        }

        super.validate(articleReactionDto.reactionDto)
    }
}
