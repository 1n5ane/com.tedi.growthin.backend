package com.tedi.growthin.backend.utils.exception.validation.articles

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class ArticleException extends ValidationException {

    def ArticleException() {}

    def ArticleException(String message) {
        super(message)
    }
}
