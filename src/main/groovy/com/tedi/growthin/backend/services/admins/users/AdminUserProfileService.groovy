package com.tedi.growthin.backend.services.admins.users

import com.tedi.growthin.backend.domains.users.UserProfile
import com.tedi.growthin.backend.dtos.profiles.UserProfileDto
import com.tedi.growthin.backend.services.users.UserProfileService
import org.springframework.stereotype.Service

@Service
class AdminUserProfileService extends UserProfileService {


    AdminUserProfileService() {}

    List<UserProfileDto> findAllByUserIds(List<Long> userIds) throws Exception {
        List< UserProfile> userProfileList = this.userProfileRepository.findAllByIdsIn(userIds)
        List<UserProfileDto> userProfileDtoList = []

        userProfileList.each{up ->
            userProfileDtoList.add(userProfileDtoFromUserProfile(up))
        }
        return userProfileDtoList
    }

}
