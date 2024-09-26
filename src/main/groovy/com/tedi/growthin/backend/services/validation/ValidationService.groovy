package com.tedi.growthin.backend.services.validation

import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import org.springframework.stereotype.Component

@Component
interface ValidationService {

    public void validate(def request) throws ValidationException
}