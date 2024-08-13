package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.users.UserProfile
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.profiles.UserProfileDto
import com.tedi.growthin.backend.dtos.profiles.UserProfileListDto
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserProfileService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import com.tedi.growthin.backend.utils.exception.validation.profiles.UserProfileException
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

        if (optionalUserProfile.isPresent()) {
            throw new UserProfileException("User with id '${currentLoggedInUserId}' has already a profile.")
        }

        //if not exists -> register
        def userProfile = userProfileService.createUserProfile(userProfileDto)
        return UserProfileService.userProfileDtoFromUserProfile(userProfile)
    }

    // udpate profile
    UserProfileDto updateUserProfile(UserProfileDto userProfileDto, Authentication authentication) throws Exception {
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
        //if not exists -> register
        def userProfile = userProfileService.updateUserProfile(userProfileDto)
        return UserProfileService.userProfileDtoFromUserProfile(userProfile)
    }

    //findById or username
    UserProfileDto getUserProfileByIdOrUsername(UserProfileDto userProfileDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        def optionalUserProfile
        def findByField
        def findByFieldValue
        if (userProfileDto.id != null) {
            //find by id
            findByField = "id"
            findByFieldValue = userProfileDto.id
            optionalUserProfile = userProfileService.findById((Long) userProfileDto.id)
        } else if (userProfileDto.username != null && !userProfileDto.username.isEmpty()) {
            //find by username
            findByField = "username"
            findByFieldValue = userProfileDto.username
            optionalUserProfile = userProfileService.findByUsername(userProfileDto.username)
        } else {
            throw new UserProfileException("No id or username provided")
        }

        if (optionalUserProfile.isEmpty()) {
            log.trace("User profile with ${findByField} '${findByFieldValue}' was not found")
            return null
        }

        def profileDto = UserProfileService.userProfileDtoFromUserProfile(optionalUserProfile.get())
        return hidePrivateFieldsIfNotConnected(currentLoggedInUserId, profileDto)

    }

    List<UserProfileDto> getUserProfilesByIdsOrUsernames(List<Long> ids, List<String> usernames, authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        def findByField
        def findByFieldValue
        List<UserProfile> userProfiles

        if (ids != null && !ids.isEmpty()) {
            findByField = "ids"
            findByFieldValue = ids
            userProfiles = userProfileService.findByIdsIn(ids)
        } else if (usernames != null && !usernames.isEmpty()) {
            findByField = "usernames"
            findByFieldValue = usernames
            userProfiles = userProfileService.findByUsernamesIn(usernames)
        } else {
            throw new UserProfileException("No ids or usernames provided")
        }

        if (userProfiles.isEmpty()) {
            log.trace("User profiles with ${findByField} ${findByFieldValue} were not found")
            return []
        }

        def userProfileDtos = []
        //check with which profiles user is connected with
        userProfiles.each { UserProfile up ->
            userProfileDtos.add(UserProfileService.userProfileDtoFromUserProfile(up))
        }

        return hidePrivateFieldsIfNotConnected(currentLoggedInUserId, userProfileDtos)
    }

    private List<UserProfileDto> hidePrivateFieldsIfNotConnected(Long userId, List<UserProfileDto> userProfileDtoList) throws Exception {
        def profileIds = [] //profileIds always equals with user ids
        userProfileDtoList.each { up ->
            profileIds.add(up.id)
        }

        def connectedProfileIds = userConnectionService.getConnectedUserIdsFromIdList(userId, profileIds)

        userProfileDtoList.eachWithIndex { UserProfileDto up, int i ->
            if (!connectedProfileIds.contains(up.id) && up.id != userId) {
                up = UserProfileDto.hidePrivateFields(up)
                userProfileDtoList[i] = up
            }
        }

        return userProfileDtoList
    }

    //check if userId are connected with userProfile user and hide private fields in case they are not
    private UserProfileDto hidePrivateFieldsIfNotConnected(Long userId, UserProfileDto userProfileDto) throws Exception {

        //if user fetches his own profile
        if (userId == userProfileDto.id)
            return userProfileDto

        //else check if two users are connected
        Boolean usersConnected = userConnectionService.checkUserConnectionExists(
                new UserConnectionDto(null, userId, userProfileDto.id)
        )

        if (!usersConnected) {
            userProfileDto = UserProfileDto.hidePrivateFields(userProfileDto)
        }

        return userProfileDto
    }


    //list all
    UserProfileListDto findAllUserProfiles(Integer page,
                                           Integer pageSize,
                                           String sortBy,
                                           String order,
                                           Authentication authentication) throws Exception {


        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "username", "createdAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, username, createdAt]")

        if (sortBy == "id")
            sortBy = "profileId"
        else if (sortBy == "username")
            sortBy = "user.username"

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        def userProfilesPage = userProfileService.listAllUserProfiles(page, pageSize, sortBy, order)

        UserProfileListDto userProfileListDto = new UserProfileListDto()
        userProfileListDto.totalPages = userProfilesPage.totalPages

        if (userProfilesPage.isEmpty()) {
            return userProfileListDto
        }

        List<UserProfile> userProfileList = userProfilesPage.getContent()

        def userProfileDtos = []
        userProfileList.each { UserProfile up ->
            userProfileDtos.add(UserProfileService.userProfileDtoFromUserProfile(up))
        }
        //check with which profiles user is connected with and return with hidden fields
        userProfileListDto.userProfiles = hidePrivateFieldsIfNotConnected(currentLoggedInUserId, userProfileDtos)
        return userProfileListDto
    }

    //search by username
    UserProfileListDto searchAllUserProfilesByUsername(String username,
                                                       Integer page,
                                                       Integer pageSize,
                                                       String sortBy,
                                                       String order,
                                                       Authentication authentication) throws Exception {


        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "username", "createdAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, username, createdAt]")

        if (sortBy == "id")
            sortBy = "profileId"
        else if (sortBy == "username")
            sortBy = "user.username"

        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        def userProfilesPage = userProfileService.searchUserProfilesByUsername(username, page, pageSize, sortBy, order)

        UserProfileListDto userProfileListDto = new UserProfileListDto()
        userProfileListDto.totalPages = userProfilesPage.totalPages

        if (userProfilesPage.isEmpty()) {
            return userProfileListDto
        }

        List<UserProfile> userProfileList = userProfilesPage.getContent()

        def userProfileDtos = []
        userProfileList.each { UserProfile up ->
            userProfileDtos.add(UserProfileService.userProfileDtoFromUserProfile(up))
        }
        //check with which profiles user is connected with and return with hidden fields
        userProfileListDto.userProfiles = hidePrivateFieldsIfNotConnected(currentLoggedInUserId, userProfileDtos)
        return userProfileListDto
    }

}
