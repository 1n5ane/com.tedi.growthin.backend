package com.tedi.growthin.backend.services.validation.profiles

import com.tedi.growthin.backend.dtos.profiles.UserProfileDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import com.tedi.growthin.backend.utils.exception.validation.profiles.UserProfileException
import groovy.util.logging.Slf4j
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserProfileValidationService implements ValidationService{

    void validate(def userProfileDto) throws ValidationException {
        if (userProfileDto == null)
            throw new UserProfileException("User profile can't be empty")

        userProfileDto = userProfileDto as UserProfileDto

        if(userProfileDto.age!=null){
            try{
                userProfileDto.age = (userProfileDto.age as String).toLong()
            }catch(NumberFormatException ignored){
                throw new UserProfileException("Age is invalid")
            }
        }

        if(userProfileDto.age!=null && (userProfileDto.age <6 || userProfileDto.age>110))
            throw new UserProfileException("Age is invalid.")

        if(userProfileDto.country?.length()>50)
            throw new UserProfileException("Country can't be more than 50 characters")
    }
}
