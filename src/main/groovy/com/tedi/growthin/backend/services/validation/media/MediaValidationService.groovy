package com.tedi.growthin.backend.services.validation.media

import com.tedi.growthin.backend.dtos.media.MediaDto
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.validation.media.MediaException
import org.springframework.stereotype.Service

@Service
class MediaValidationService implements ValidationService{

    @Override
    void validate(def request) throws MediaException {
        if(request == null){
            throw new MediaException("Media can't be empty")
        }

        request = request as MediaDto

        if(request.data == null || request.data.length == 0){
            throw new MediaException("Media data can't be empty")
        }

        if(request.userId == null){
            throw new MediaException("User id in media can't be empty. Media must have an owner.")
        }
    }
}
