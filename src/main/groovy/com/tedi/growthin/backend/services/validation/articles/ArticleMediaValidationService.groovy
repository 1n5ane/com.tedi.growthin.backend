package com.tedi.growthin.backend.services.validation.articles

import com.tedi.growthin.backend.dtos.articles.ArticleMediaDto
import com.tedi.growthin.backend.services.validation.media.MediaValidationService
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleMediaException
import org.springframework.stereotype.Service

@Service
class ArticleMediaValidationService extends MediaValidationService {

    @Override
    void validate(def articleMediaDto) throws Exception {
        if(articleMediaDto == null){
            throw new ArticleMediaException("Article media can't be empty")
        }

        articleMediaDto = articleMediaDto as ArticleMediaDto

        if(articleMediaDto.order == null){
            throw new ArticleMediaException("Article's media order must be set. Order can't be empty")
        }

        super.validate(articleMediaDto.mediaDto)
    }
}
