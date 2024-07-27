package com.tedi.growthin.backend.utils.exception.validation.paging

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
class PagingArgumentException extends ValidationException{
    PagingArgumentException() {}

    PagingArgumentException(String message) {
        super(message)
    }
}
