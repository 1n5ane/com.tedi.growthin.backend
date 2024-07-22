package com.tedi.growthin.backend.utils.exception

class UserRoleNotExistsException extends ValidationException{

    UserRoleNotExistsException() {
        super()
    }

    UserRoleNotExistsException(String message) {
        super(message)
    }
}
