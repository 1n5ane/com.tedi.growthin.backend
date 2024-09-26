package com.tedi.growthin.backend.utils.exception.validation.articles

import com.tedi.growthin.backend.utils.exception.validation.media.MediaException
import org.springframework.stereotype.Component

@Component
class ArticleMediaException extends MediaException {

    ArticleMediaException() {}

    ArticleMediaException(String message) {
        super(message)
    }
}
