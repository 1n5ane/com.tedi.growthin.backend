package com.tedi.growthin.backend.services.validation.userProfiles

import com.tedi.growthin.backend.dtos.userProfiles.UserProfileDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.userProfiles.UserProfileException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserProfileValidationService implements ValidationService{

    void validate(def userProfileDto) throws ValidationException {
        if (userProfileDto == null)
            throw new UserProfileException("User profile can't be empty")

        userProfileDto = userProfileDto as UserProfileDto

        if(userProfileDto.age!=null && (userProfileDto.age <6 || userProfileDto.age>110))
            throw new UserProfileException("Age is invalid.")

        if(userProfileDto.country?.length()>50)
            throw new UserProfileException("Country can't be more than 50 characters")
    }
}
