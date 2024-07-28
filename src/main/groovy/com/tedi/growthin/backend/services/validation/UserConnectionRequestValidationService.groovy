package com.tedi.growthin.backend.services.validation

import com.tedi.growthin.backend.dtos.users.UserConnectionRequestDto
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.connections.UserConnectionRequestException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserConnectionRequestValidationService implements ValidationService {

    @Override
    void validate(def request) throws ValidationException {

        if(request == null)
            throw new UserConnectionRequestException("Data cannot be empty")

        request = request as UserConnectionRequestDto

        if(request.userId == null || (request.userId as String).isEmpty())
            throw new UserConnectionRequestException("userId can't be empty")

        if(request.connectedUserId == null || (request.connectedUserId as String).isEmpty())
            throw new UserConnectionRequestException("connectedUserId can't be empty")

        if(request.userId == request.connectedUserId)
            throw new UserConnectionRequestException("Users can't connect with themselves")

    }
}
