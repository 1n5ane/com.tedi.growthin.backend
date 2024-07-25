package com.tedi.growthin.backend.utils.exception.validation.users

import com.tedi.growthin.backend.utils.exception.validation.ValidationException

class UserRoleNotExistsException extends ValidationException{

    UserRoleNotExistsException() {
        super()
    }

    UserRoleNotExistsException(String message) {
        super(message)
    }
}
