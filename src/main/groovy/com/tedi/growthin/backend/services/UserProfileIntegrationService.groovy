package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.users.UserProfile
import com.tedi.growthin.backend.dtos.userProfiles.UserProfileDto
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserProfileService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.userProfiles.UserProfileException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserProfileIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserConnectionService userConnectionService

    @Autowired
    UserProfileService userProfileService


    //register profile
    UserProfileDto registerUserProfile(UserProfileDto userProfileDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        // user/admin registers only his own profile
        userProfileDto.id = currentLoggedInUserId
        validationServiceMap["userProfileValidationService"].validate(userProfileDto)

        if (userProfileDto.profilePic) {
            userProfileDto.profilePic.userId = currentLoggedInUserId
            validationServiceMap["mediaValidationService"].validate(userProfileDto.profilePic)
        }

        if (userProfileDto.cvDocument) {
            userProfileDto.cvDocument.userId = currentLoggedInUserId
            validationServiceMap["mediaValidationService"].validate(userProfileDto.cvDocument)
        }
        //check if user profile already created
        Optional<UserProfile> optionalUserProfile = userProfileService.findById(currentLoggedInUserId)

        if(optionalUserProfile.isPresent()){
            throw new UserProfileException("User with id '${currentLoggedInUserId}' has already a profile.")
        }

        //if not exists -> register
        def userProfile = userProfileService.createUserProfile(userProfileDto)
        return UserProfileService.userProfileDtoFromUserProfile(userProfile)
    }

    //TODO: check if users are not connected when fetching a user profile-> only return public profile fields

    //list all

    //search by username

    //findById

    //update own profile details

}
