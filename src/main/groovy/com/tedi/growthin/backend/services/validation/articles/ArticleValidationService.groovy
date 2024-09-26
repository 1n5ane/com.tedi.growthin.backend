package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleException
import org.springframework.stereotype.Service

@Service
class ArticleValidationService implements ValidationService {
    @Override
    void validate(def articleDto) throws ValidationException {
        if (articleDto == null)
            throw new ArticleException("Article can't be empty")

        articleDto = articleDto as ArticleDto

        if (articleDto.userDto == null)
            throw new ArticleException("Article user owner can't be empty")

        if (checkIsEmpty(articleDto.body) && checkIsEmpty(articleDto.articleMedias))
            throw new ArticleException("Article must have a body or medias attached")

        if (!checkIsEmpty(articleDto.title) && checkNotIsRange(articleDto.title, 1, 512))
            throw new ArticleException("Article title must be between 1 and 512 characters long")

        if(articleDto.publicStatus == null)
            throw new ArticleException("Article's public status can't be null")
    }

    private static Boolean checkIsEmpty(def data) {
        return (data == null || data.isEmpty())
    }

    private static Boolean checkNotIsRange(String data, Integer lowerBound, Integer higherBound) {
        return data.size() < lowerBound || data.size() > higherBound
    }

    private static Boolean checkContainsInvalidChars(String data, List<String> invalidChars) {
        return invalidChars.any { c -> data.contains(c) }
    }
}
