package com.tedi.growthin.backend.services.validation.users

import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.users.UserValidationException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserValidationService implements ValidationService {
    static final invalidChars = ["@", "'", "\"", "/", "\\", "[",
                                 "]", ":", ";", "|", "=", "+",
                                 "*", "?", "<", ">", "#", "\$",
                                 "^", "&", "*", "(", ")"]

    @Override
    void validate(def request) throws UserValidationException {
        if (request == null)
            throw new UserValidationException("Data cannot be empty")

//      Cast to UserDto
        request = request as UserDto

//      Username check
        if (checkIsEmpty(request.username))
            throw new UserValidationException("Username cannot be empty")

//      all size check numbers are originated from db constraints
        if (checkNotIsRange(request.username, 2, 25))
            throw new UserValidationException("Username must be between 2 and 25 characters")

        if (checkContainsInvalidChars(request.username, invalidChars))
            throw new UserValidationException("Username contains invalid characters")

//       Email check
        if (checkIsEmpty(request.email))
            throw new UserValidationException("Email cannot be empty")

        if (checkNotIsRange(request.email, 5, 255))
            throw new UserValidationException("Email must be between 5 and 255 characters")

        if (!(request.email).contains("@"))
            throw new UserValidationException("Email is invalid")

//      first name check
        if (checkIsEmpty(request.name))
            throw new UserValidationException("Name cannot be empty")

        if (checkNotIsRange(request.name, 1, 255))
            throw new UserValidationException("Name must be between 1 and 255 characters")

//      surname check
        if (checkIsEmpty(request.surname))
            throw new UserValidationException("Surname cannot be empty")

        if (checkNotIsRange(request.surname, 1, 255))
            throw new UserValidationException("Surname must be between 1 and 255 characters")

//     if invalid roles -> exception will be thrown
    }

    static Boolean usernameIsValid(String username) {
        return !checkIsEmpty(username) &&
                !checkNotIsRange(username, 2, 25)
    }

    static Boolean emailIsValid(String email) {
        return !checkIsEmpty(email) &&
                !checkNotIsRange(email, 5, 255) &&
                email.contains("@")
    }

    private static Boolean checkIsEmpty(String data) {
        return (data == null || data.isEmpty())
    }

    private static Boolean checkNotIsRange(String data, Integer lowerBound, Integer higherBound) {
        return data.size() < lowerBound || data.size() > higherBound
    }

    private static Boolean checkContainsInvalidChars(String data, List<String> invalidChars) {
        return invalidChars.any { c -> data.contains(c) }
    }
}