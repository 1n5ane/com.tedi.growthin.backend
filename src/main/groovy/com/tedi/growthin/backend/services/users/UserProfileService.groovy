package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserProfile
import com.tedi.growthin.backend.dtos.userProfiles.UserProfileDto
import com.tedi.growthin.backend.repositories.users.UserProfileRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.utils.exception.validation.userProfiles.UserProfileException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime
import java.util.function.Consumer

@Service
@Slf4j
class UserProfileService {

    @Autowired
    UserProfileRepository userProfileRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    MediaService mediaService

    @Transactional(rollbackFor = Exception.class)
    UserProfile createUserProfile(UserProfileDto userProfileDto) throws Exception {
        //nested @Transactional will not create seperate transaction on nested calls
        // all these will be in a single transaction
        // in case of exception -> rollback everything

        Media profilePicMedia = null
        if (userProfileDto.profilePic) {
            profilePicMedia = mediaService.createMedia(userProfileDto.profilePic)
        }

        Media cvDocumentMedia = null
        if (userProfileDto.cvDocument) {
            cvDocumentMedia = mediaService.createMedia(userProfileDto.profilePic)
        }
//        User profileUser = userRepository.findById((Long) userProfileDto.id).get()
        User profileUser = new User()
        profileUser.id = (Long) userProfileDto.id
        UserProfile userProfile = new UserProfile(
                profileUser,
                userProfileDto.jobField,
                profilePicMedia,
                cvDocumentMedia,
                userProfileDto.description,
                (Integer) userProfileDto.age,
                userProfileDto.education,
                userProfileDto.country,
                userProfileDto.isJobFieldPublic != null ? userProfileDto.isJobFieldPublic : false,
                userProfileDto.isCvDocumentPublic != null ? userProfileDto.isCvDocumentPublic : false,
                userProfileDto.isDescriptionPublic != null ? userProfileDto.isDescriptionPublic : false,
                userProfileDto.isAgePublic != null ? userProfileDto.isAgePublic : false,
                userProfileDto.isEducationPublic != null ? userProfileDto.isEducationPublic : false,
                userProfileDto.isCountryPublic != null ? userProfileDto.isCountryPublic : false
        )

        return userProfileRepository.save(userProfile)
    }

    @Transactional(rollbackFor = Exception.class)
    UserProfile updateUserProfile(UserProfileDto userProfileDto) throws Exception {
        //nested @Transactional will not create seperate transaction on nested calls
        // all these will be in a single transaction
        // in case of exception -> rollback everything

        //check if user profile already created
        Optional<UserProfile> optionalUserProfile = this.findById((Long) userProfileDto.id)

        if (optionalUserProfile.isEmpty()) {
            throw new UserProfileException("User with id '${userProfileDto.id}' has not registered a profile. Please register one before updating")
        }

        def updated = false
        def userProfile = optionalUserProfile.get()

        if (userProfileDto.profilePic) {
            if (userProfile.profilePicMedia && userProfile.profilePicMedia.data != userProfileDto.profilePic.data) {
                Media profilePicMedia = mediaService.createMedia(userProfileDto.profilePic)
                userProfile.profilePicMedia = profilePicMedia
                updated = true
            }
        }

        if (userProfileDto.cvDocument) {
            if (userProfile.cvDocumentMedia && userProfile.cvDocumentMedia.data != userProfileDto.cvDocument.data) {
                Media cvDocumentMedia = mediaService.createMedia(userProfileDto.profilePic)
                userProfile.cvDocumentMedia = cvDocumentMedia
                updated = true
            }
        }

        updated = updateField(userProfile::setJobField, userProfile.getJobField(), userProfileDto.getJobField(), updated)
        updated = updateField(userProfile::setIsJobFieldPublic, userProfile.getIsJobFieldPublic(), userProfileDto.getIsJobFieldPublic(), updated)
        updated = updateField(userProfile::setIsCvDocumentPublic, userProfile.getIsCvDocumentPublic(), userProfileDto.getIsCvDocumentPublic(), updated)
        updated = updateField(userProfile::setDescription, userProfile.getDescription(), userProfileDto.getDescription(), updated)
        updated = updateField(userProfile::setIsDescriptionPublic, userProfile.getIsDescriptionPublic(), userProfileDto.getIsDescriptionPublic(), updated)
        updated = updateField(userProfile::setAge, userProfile.getAge(), (Integer) userProfileDto.getAge(), updated)
        updated = updateField(userProfile::setIsAgePublic, userProfile.getIsAgePublic(), userProfileDto.getIsAgePublic(), updated)
        updated = updateField(userProfile::setEducation, userProfile.getEducation(), userProfileDto.getEducation(), updated)
        updated = updateField(userProfile::setIsEducationPublic, userProfile.getIsEducationPublic(), userProfileDto.getIsEducationPublic(), updated)
        updated = updateField(userProfile::setCountry, userProfile.getCountry(), userProfileDto.getCountry(), updated)
        updated = updateField(userProfile::setIsCountryPublic, userProfile.getIsCountryPublic(), userProfileDto.getIsCountryPublic(), updated)

        if (updated) {
            userProfile.setUpdatedAt(OffsetDateTime.now())
            userProfile = userProfileRepository.save(userProfile)
        } else{
            log.trace("Profile with id '${userProfile.profileId}' was not updated no changes made")
        }
        return userProfile
    }

    private <T> boolean updateField(Consumer<T> setter, T currentValue, T newValue, boolean updated) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue)
            updated = true
        }
        return updated
    }


    Optional<UserProfile> findById(Long id) throws Exception {
        return userProfileRepository.findById(id)
    }

    static UserProfileDto userProfileDtoFromUserProfile(UserProfile userProfile) {
        return new UserProfileDto(
                (Long) userProfile.profileId,
                userProfile.jobField,
                userProfile.profilePicMedia ? MediaService.mediaDtoFromMedia(userProfile.profilePicMedia) : null,
                userProfile.cvDocumentMedia ? MediaService.mediaDtoFromMedia(userProfile.cvDocumentMedia) : null,
                userProfile.description,
                (Integer) userProfile.age,
                userProfile.education,
                userProfile.country,
                userProfile.isJobFieldPublic,
                userProfile.isCvDocumentPublic,
                userProfile.isDescriptionPublic,
                userProfile.isAgePublic,
                userProfile.isEducationPublic,
                userProfile.isCountryPublic,
                userProfile.createdAt,
                userProfile.updatedAt
        )
    }
}
