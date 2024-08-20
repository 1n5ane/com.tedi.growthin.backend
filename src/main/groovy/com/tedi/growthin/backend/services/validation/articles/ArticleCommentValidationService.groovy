package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentException
import org.springframework.stereotype.Service

@Service
class ArticleCommentValidationService implements ValidationService {

    @Override
    void validate(def articleCommentDto) throws ValidationException {
        if(articleCommentDto == null)
            throw new ArticleCommentException("Article comment can't be empty")

        articleCommentDto = articleCommentDto as ArticleCommentDto

        if(articleCommentDto.userDto == null || articleCommentDto.userDto.id == null){
            throw new ArticleCommentException("User id not provided for comment")
        }

        if(articleCommentDto.articleId == null)
            throw new ArticleCommentException("Article id not provided for comment")

        if(articleCommentDto.comment == null || articleCommentDto.comment.isEmpty())
            throw new ArticleCommentException("Article comment body can't be empty")
    }
}
