package com.tedi.growthin.backend.services.users

import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.domains.users.UserProfile
import com.tedi.growthin.backend.dtos.userProfiles.UserProfileDto
import com.tedi.growthin.backend.repositories.users.UserProfileRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
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
        User profileUser = userRepository.findById((Long) userProfileDto.id).get()
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
