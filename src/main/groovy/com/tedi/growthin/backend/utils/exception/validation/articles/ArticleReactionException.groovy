package com.tedi.growthin.backend.utils.exception.validation.articles

import com.tedi.growthin.backend.utils.exception.validation.reactions.ReactionException
import org.springframework.stereotype.Component

@Component
class ArticleReactionException extends ReactionException {

    ArticleReactionException() {}

    ArticleReactionException(String message) {
        super(message)
    }
}
