package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleException
import org.springframework.stereotype.Service

@Service
class ArticleUpdateValidationService extends ArticleValidationService {

    @Override
    void validate(def articleDto) throws ValidationException {
        //null fields mean no update -> no exception will be thrown
        if (articleDto == null)
            throw new ArticleException("Article can't be empty")

        articleDto = articleDto as ArticleDto

        if (articleDto.id == null) {
            throw new ArticleException("Article id can't be empty")
        }

        if (articleDto.userDto == null)
            throw new ArticleException("Article user owner can't be empty")

        if (articleDto.title != null && articleDto.title.size() > 512) {
            throw new ArticleException("Article title must be smaller than 512 characters long")
        }

        //body cannot be set to ""
        if(articleDto.body != null && articleDto.body.size()==0) {
            throw new ArticleException("Article must have a body. Empty body provided.")
        }
    }

    private static Boolean checkNotIsRange(String data, Integer lowerBound, Integer higherBound) {
        return data.size() < lowerBound || data.size() > higherBound
    }
}
