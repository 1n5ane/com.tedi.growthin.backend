package com.tedi.growthin.backend.services.media

import com.tedi.growthin.backend.domains.media.Media
import com.tedi.growthin.backend.domains.media.MediaType
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.media.MediaDto
import com.tedi.growthin.backend.dtos.media.MediaTypeDto
import com.tedi.growthin.backend.repositories.media.MediaRepository
import com.tedi.growthin.backend.repositories.media.MediaTypeRepository
import com.tedi.growthin.backend.utils.exception.validation.media.MediaException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MediaService {

    @Autowired
    MediaTypeRepository mediaTypeRepository

    @Autowired
    MediaRepository mediaRepository

    @Transactional(rollbackFor = Exception.class)
    Media createMedia(MediaDto mediaDto) throws Exception {
        if(mediaDto.mediaTypeDto!=null && (mediaDto.mediaTypeDto.name==null || mediaDto.mediaTypeDto.name.isEmpty())){
            throw new MediaException("Media type can't be empty")
        }

        //search media type to fetch id
        def mediaType = mediaTypeRepository.findByNameContaining(mediaDto.mediaTypeDto.name)

        if(mediaType == null){
            throw new MediaException("Media type '${mediaDto.mediaTypeDto.name}' is unknown")
        }

        mediaDto.mediaTypeDto.id = mediaType.id
        mediaDto.mediaTypeDto.name = mediaType.name

        Media media = mediaFromMediaDto(mediaDto)
        media = mediaRepository.save(media)
        return media
    }

    @Transactional(rollbackFor = Exception.class)
    Media updateDeleted(Integer mediaId, Boolean markDeleted = true) throws Exception {
        //return null in case of no update
        Optional<Media> optionalMedia = mediaRepository.findById(mediaId)

        if (optionalMedia.isEmpty()) {
            throw new MediaException("Media with id '${mediaId}' was not found.")
        }

        Media media = optionalMedia.get()
        if (markDeleted == null)
            markDeleted = false

        if (media.isDeleted != markDeleted){
            media.isDeleted = markDeleted
            media = mediaRepository.save(media)
        }else {
            media = null
        }

        return media
    }

    Optional<Media> findById(Long id) throws Exception {
        return mediaRepository.findById(id)
    }

    List<Media> findAllByIds(List<Long> ids){
        if(ids.isEmpty())
            return []

        return mediaRepository.findAllByIdIn(ids)
    }

    static Media mediaFromMediaDto(MediaDto mediaDto) {
        User mediaUser = new User()
        mediaUser.id = mediaDto.userId

        MediaType mediaType = new MediaType(
                (Integer) mediaDto.mediaTypeDto.id,
                mediaDto.mediaTypeDto.name
        )
        Media media = new Media(
                (Long) mediaDto.id,
                mediaUser,
                mediaType,
                mediaDto.data,
                mediaDto.isDeleted
        )
        return media
    }

    static MediaDto mediaDtoFromMedia(Media media) {
        return new MediaDto(
                media.id,
                media.user.id,
                new MediaTypeDto(media.mediaType.id, media.mediaType.name),
                media.data,
                media.isDeleted,
                media.createdAt
        )
    }
}
