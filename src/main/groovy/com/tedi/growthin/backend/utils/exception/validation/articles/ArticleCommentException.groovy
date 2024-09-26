package com.tedi.growthin.backend.utils.exception.validation.articles

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class ArticleCommentException extends ValidationException {

    def ArticleCommentException() {}

    def ArticleCommentException(String message) {
        super(message)
    }
}
