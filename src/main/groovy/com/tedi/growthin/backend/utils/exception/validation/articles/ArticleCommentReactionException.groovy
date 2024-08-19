package com.tedi.growthin.backend.utils.exception.validation.articles

import com.tedi.growthin.backend.utils.exception.validation.reactions.ReactionException
import org.springframework.stereotype.Component

@Component
class ArticleCommentReactionException extends ReactionException {

    ArticleCommentReactionException() {}

    ArticleCommentReactionException(String message) {
        super(message)
    }
}
