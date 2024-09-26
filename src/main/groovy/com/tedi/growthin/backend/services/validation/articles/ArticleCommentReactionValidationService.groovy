package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.services.validation.reactions.ReactionValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentReactionException
import org.springframework.stereotype.Service

@Service
class ArticleCommentReactionValidationService extends ReactionValidationService {

    @Override
    void validate(def articleCommentReactionDto) throws ValidationException {
        if(articleCommentReactionDto == null){
            throw new ArticleCommentReactionException("Article comment reaction can't be empty")
        }

        articleCommentReactionDto = articleCommentReactionDto as ArticleCommentReactionDto

        if(articleCommentReactionDto.articleId == null){
            throw new ArticleCommentReactionException("Article id can't be empty")
        }

        if(articleCommentReactionDto.commentId == null){
            throw new ArticleCommentReactionException("Comment id can't be empty")
        }

        if(articleCommentReactionDto.userDto == null || articleCommentReactionDto.userDto.id == null){
            throw new ArticleCommentReactionException("User of article comment reaction can't be empty")
        }

        super.validate(articleCommentReactionDto.reactionDto)
    }
}
