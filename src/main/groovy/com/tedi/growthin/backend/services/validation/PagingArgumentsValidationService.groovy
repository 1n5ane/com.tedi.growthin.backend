package com.tedi.growthin.backend.services.validation

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class PagingArgumentsValidationService implements ValidationService {
    @Override
    void validate(def arguments) throws ValidationException {
        if (arguments["page"] < 0) throw new PagingArgumentException("Page number can't be negative!")
        if (arguments["pageSize"] <= 0) throw new PagingArgumentException("Page size can't be negative or zero")
        if (arguments["pageSize"] > 100) throw new PagingArgumentException("Page size can't be more than 100")

        def order = arguments["order"] as String
        if (!["asc", "desc"].contains(order))
            throw new PagingArgumentException("Order can only be 'asc' or 'desc'")
    }
}
