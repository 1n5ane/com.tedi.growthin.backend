package com.tedi.growthin.backend.services.articles

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.articles.ArticleCommentReaction
import com.tedi.growthin.backend.domains.articles.ArticleMedia
import com.tedi.growthin.backend.domains.articles.ArticleReaction
import com.tedi.growthin.backend.domains.enums.PublicStatus
import com.tedi.growthin.backend.domains.users.User
import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleMediaDto
import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.dtos.notifications.NotificationDto
import com.tedi.growthin.backend.dtos.notifications.NotificationTypeDto
import com.tedi.growthin.backend.dtos.reactions.ReactionDto
import com.tedi.growthin.backend.repositories.articles.ArticleCommentReactionRepository
import com.tedi.growthin.backend.repositories.articles.ArticleCommentRepository
import com.tedi.growthin.backend.repositories.articles.ArticleMediaRepository
import com.tedi.growthin.backend.repositories.articles.ArticleReactionRepository
import com.tedi.growthin.backend.repositories.articles.ArticleRepository
import com.tedi.growthin.backend.repositories.reactions.ReactionRepository
import com.tedi.growthin.backend.repositories.users.UserConnectionRepository
import com.tedi.growthin.backend.repositories.users.UserRepository
import com.tedi.growthin.backend.services.media.MediaService
import com.tedi.growthin.backend.services.notifications.NotificationService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentReactionException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleMediaException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleReactionException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.OffsetDateTime
import java.util.function.Consumer

@Service
@Slf4j
class UserArticleService {

    @Autowired
    UserRepository userRepository

    @Autowired
    MediaService mediaService

    @Autowired
    ReactionRepository reactionRepository

    @Autowired
    ArticleRepository articleRepository

    @Autowired
    ArticleReactionRepository articleReactionRepository

    @Autowired
    ArticleCommentRepository articleCommentRepository

    @Autowired
    ArticleCommentReactionRepository articleCommentReactionRepository

    @Autowired
    ArticleMediaRepository articleMediaRepository

    @Autowired
    UserConnectionRepository userConnectionRepository

    //THIS BREAKS THE 'S' FROM SOLID PRINCIPLE (SINGLE RESPONSIBILITY)
    //The notification logic shouldn't be here
    //It should be called from the caller of this service
    //THIS IS DONE FOR THE SAKE OF ASSIGNMENT (no time for setting up a robust notification system with decoupled architecture...)
    //BECAUSE WE WANT NOTIFICATION TO BE SENT IN THE SAME TRANSACTION
    // so in case of app restart or in case of failed notification -> also rollback action (comment, commentReaction, articleReaction) so that client can retry....
    //If this was meant for production we would need a whole different architecture for event handling (Redis, activeMq etc...)
    //So when the caller gets succes when publishing a message -> the message would be 100% delivered [at least once (if not exactly once )] <- that's a whole different story
    @Autowired
    NotificationService notificationService

    @Transactional(rollbackFor = Exception.class)
    ArticleDto createNewArticle(ArticleDto articleDto) throws Exception {
        if (articleDto.userDto == null)
            throw new ArticleException("User owner of article can't be empty")

        Optional<User> optionalUser = userRepository.findById((Long) articleDto.userDto.id)

        if (optionalUser.isEmpty())
            throw new ArticleException("User with id '${articleDto.userDto.id}' was not found")


        Article article = new Article(
                optionalUser.get(),
                articleDto.title,
                articleDto.body,
                [],
                [],
                [],
                articleDto.publicStatus,
                false
        )

        article = articleRepository.save(article)

        //create articles media (given article id)
        List<ArticleMedia> articleMedias = []
        if (articleDto.articleMedias != null && !articleDto.articleMedias.isEmpty()) {
            articleMedias = this.createArticleMedias(article.id, articleDto.articleMedias)
        }
        article.articleMedias = articleMedias
        return articleDtoFromArticle(article)
    }

    //create or update
    @Transactional(rollbackFor = Exception.class)
    ArticleReactionDto createNewArticleReaction(ArticleReactionDto articleReactionDto, Boolean notify = true) throws Exception {
        if (articleReactionDto.reactionDto == null || (articleReactionDto.reactionDto.id == null && (articleReactionDto.reactionDto.alias == null || articleReactionDto.reactionDto.alias.isEmpty()))) {
            throw new ArticleReactionException("No reaction provided by user with id '${articleReactionDto.userDto.id}' for reacting to article with id '${articleReactionDto.articleId}'")
        }

        //fetch article reaction if already exists
        Optional<ArticleReaction> optionalArticleReaction = articleReactionRepository.findByArticleIdAndUserId(
                (Long) articleReactionDto.articleId,
                (Long) articleReactionDto.userDto.id
        )

        //fetch reaction
        def optionalReaction
        def reactionReference
        if (articleReactionDto.reactionDto.id != null) {
            optionalReaction = reactionRepository.findById((Long) articleReactionDto.reactionDto.id)
            reactionReference = articleReactionDto.reactionDto.id
        } else {
            optionalReaction = reactionRepository.findByAlias(articleReactionDto.reactionDto.alias)
            reactionReference = articleReactionDto.reactionDto.alias
        }

        if (optionalReaction.isEmpty()) {
            throw new ArticleReactionException("Reaction with reference '${reactionReference}' was not found")
        }

        def updated = false
        ArticleReaction newArticleReaction
        if (optionalArticleReaction.isEmpty()) {
            //if this is a new reaction
            def article = new Article()
            article.id = articleReactionDto.articleId

            //fetch user
            def optionalUser = userRepository.findById((Long) articleReactionDto.userDto.id)
            if (optionalUser.isEmpty())
                throw new ArticleReactionException("User with id '${articleReactionDto.userDto.id}' was not found for article reacting.")

            newArticleReaction = new ArticleReaction(
                    article,
                    optionalUser.get(),
                    optionalReaction.get()
            )

            newArticleReaction = articleReactionRepository.save(newArticleReaction)
        } else {
            //user has already reacted -> fetch reaction and update
            newArticleReaction = optionalArticleReaction.get()
            def reaction = optionalReaction.get()
            if (newArticleReaction.reaction.id != reaction.id || newArticleReaction.reaction.alias != reaction.alias) {
                //if not the same reaction -> update
                newArticleReaction.reaction = reaction
                newArticleReaction.updatedAt = OffsetDateTime.now()
                newArticleReaction = articleReactionRepository.save(newArticleReaction)
            }
            updated = true
        }

        //check if reaction changed to log
        if (updated) {
            log.trace("User with id '${articleReactionDto.userDto.id}' updated reaction for article with id '${articleReactionDto.articleId}'")
        } else {
            log.trace("User with id '${articleReactionDto.userDto.id}' created new reaction for article with id '${articleReactionDto.articleId}'")
        }

        ArticleReactionDto createdArticleReactionDto = articleReactionDtoFromArticleReaction(newArticleReaction)

        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        //NOTIFY LOGIC...
        if (notify) {
            //notify author of article
            //fetch article
            def a = articleRepository.findById((Long) createdArticleReactionDto.articleId).get()
            //be careful not to notify same user as the action user (ex. self like, self comment etc...)
            if (a.user.id != createdArticleReactionDto.userDto.id) {
                def notificationDto = new NotificationDto(
                        null,
                        createdArticleReactionDto.userDto,
                        UserService.userDtoFromUser(a.user),
                        new NotificationTypeDto(1, "ARTICLE_REACTION"),
                        true,
                        createdArticleReactionDto
                )
                notificationService.createNotification(notificationDto)
                log.info("Successfully created notification for article reaction")
            }
        }


        return createdArticleReactionDto
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean deleteArticleReaction(Long articleReactionId) throws Exception {
        if (articleReactionId == null) {
            throw new ArticleReactionException("Article reaction id can't be empty")
        }
        articleReactionRepository.deleteById(articleReactionId)
        return true
    }

    @Transactional(rollbackFor = Exception.class)
    ArticleDto updateArticle(ArticleDto articleDto) throws Exception {
        Optional<Article> optionalArticle = articleRepository.findById((Long) articleDto.id)
        if (optionalArticle.isEmpty()) {
            throw new ArticleException("Article with id '${articleDto.id}' was not found")
        }

        def updated = false
        def toBeUpdatedArticle = optionalArticle.get()

        if (articleDto.title != null) {
            updated = updateField(toBeUpdatedArticle::setTitle, toBeUpdatedArticle.title, articleDto.title, updated)
            if (updated && toBeUpdatedArticle.title.isEmpty()) {
                toBeUpdatedArticle.title = null
            }
        }
        //check body
        if (articleDto.body != null)
            updated = updateField(toBeUpdatedArticle::setBody, toBeUpdatedArticle.body, articleDto.body, updated)
        //check publicstatus
        if (articleDto.publicStatus != null)
            updated = updateField(toBeUpdatedArticle::setPublicStatus, toBeUpdatedArticle.publicStatus, articleDto.publicStatus, updated)

        //check media
//        if (articleDto.articleMedias != null) {
//            //TODO: update media (add or remove or change order)
//            if(articleDto.articleMedias.isEmpty()){
//                //if empty list provided
//                //remove everything from article
//            }
//        }

        def updatedArticleDto = null
        if (updated) {
            toBeUpdatedArticle.updatedAt = OffsetDateTime.now()
            toBeUpdatedArticle = articleRepository.save(toBeUpdatedArticle)
            updatedArticleDto = articleDtoFromArticle(toBeUpdatedArticle)
        } else {
            log.trace("Article with id '${toBeUpdatedArticle.id}' was not updated no changes made")
        }
        return updatedArticleDto
    }

    @Transactional(rollbackFor = Exception.class)
    //only update comment body (no delete -> there is endpoint for deleting)
    ArticleCommentDto updateArticleComment(ArticleCommentDto articleCommentDto) throws Exception {
        //check comment exists
        Optional<ArticleComment> optionalArticleComment = articleCommentRepository.findById((Long) articleCommentDto.id)
        if (optionalArticleComment.isEmpty())
            throw new ArticleCommentException("Comment with id ${articleCommentDto.id} was not found")

        def toBeUpdatedArticleComment = optionalArticleComment.get()
        def updated = false

        if (toBeUpdatedArticleComment.body != articleCommentDto.comment) {
            toBeUpdatedArticleComment.body = articleCommentDto.comment
            updated = true
        }

        def updatedCommentDto = null
        if (updated) {
            toBeUpdatedArticleComment.updatedAt = OffsetDateTime.now()
            toBeUpdatedArticleComment = articleCommentRepository.save(toBeUpdatedArticleComment)
            updatedCommentDto = articleCommentDtoFromArticleComment(toBeUpdatedArticleComment)
        } else {
            log.trace("Comment with id '${toBeUpdatedArticleComment.id}' was not updated no changes made")
        }

        return updatedCommentDto
    }

    private <T> boolean updateField(Consumer<T> setter, T currentValue, T newValue, boolean updated) {
        if (newValue != null && !newValue.equals(currentValue)) {
            setter.accept(newValue)
            updated = true
        }
        return updated
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean deleteArticleCommentReaction(Long articleCommentReactionId) throws Exception {
        if (articleCommentReactionId == null) {
            throw new ArticleCommentReactionException("Article comment reaction id can't be empty")
        }

        articleCommentReactionRepository.deleteById(articleCommentReactionId)
        return true
    }

    //create or update
    @Transactional(rollbackFor = Exception.class)
    ArticleCommentReactionDto createNewArticleCommentReaction(ArticleCommentReactionDto articleCommentReactionDto, Boolean notify = true) throws Exception {
        if (articleCommentReactionDto.reactionDto == null || (articleCommentReactionDto.reactionDto.id == null &&
                (articleCommentReactionDto.reactionDto.alias == null || articleCommentReactionDto.reactionDto.alias.isEmpty()))) {
            throw new ArticleCommentReactionException("No reaction provided by user with id '${articleCommentReactionDto.userDto.id}' for reacting to comment with id '${articleCommentReactionDto.commentId}'")
        }

        //fetch article comment reaction if already exists
        Optional<ArticleCommentReaction> optionalArticleCommentReaction = articleCommentReactionRepository.findByArticleIdAndCommentIdAndUserId(
                (Long) articleCommentReactionDto.articleId,
                (Long) articleCommentReactionDto.commentId,
                (Long) articleCommentReactionDto.userDto.id
        )

        //fetch reaction
        def optionalReaction
        def reactionReference
        if (articleCommentReactionDto.reactionDto.id != null) {
            optionalReaction = reactionRepository.findById((Long) articleCommentReactionDto.reactionDto.id)
            reactionReference = articleCommentReactionDto.reactionDto.id
        } else {
            optionalReaction = reactionRepository.findByAlias(articleCommentReactionDto.reactionDto.alias)
            reactionReference = articleCommentReactionDto.reactionDto.alias
        }

        if (optionalReaction.isEmpty()) {
            throw new ArticleCommentReactionException("Reaction with reference '${reactionReference}' was not found")
        }

        def updated = false
        ArticleCommentReaction newArticleCommentReaction
        if (optionalArticleCommentReaction.isEmpty()) {
            //if this is a new reaction
            def articleComment = new ArticleComment()
            articleComment.id = articleCommentReactionDto.commentId

            //fetch user
            def optionalUser = userRepository.findById((Long) articleCommentReactionDto.userDto.id)
            if (optionalUser.isEmpty())
                throw new ArticleReactionException("User with id '${articleCommentReactionDto.userDto.id}' was not found for comment reacting.")

            newArticleCommentReaction = new ArticleCommentReaction(
                    articleComment,
                    optionalUser.get(),
                    optionalReaction.get()
            )

            newArticleCommentReaction = articleCommentReactionRepository.save(newArticleCommentReaction)
        } else {
            //user has already reacted -> fetch reaction and update
            newArticleCommentReaction = optionalArticleCommentReaction.get()
            def reaction = optionalReaction.get()
            if (newArticleCommentReaction.reaction.id != reaction.id || newArticleCommentReaction.reaction.alias != reaction.alias) {
                //if not the same reaction -> update
                newArticleCommentReaction.reaction = reaction
                newArticleCommentReaction.updatedAt = OffsetDateTime.now()
                newArticleCommentReaction = articleCommentReactionRepository.save(newArticleCommentReaction)
                updated = true
            }
        }

        //check if reaction changed to log
        if (updated) {
            log.trace("User with id '${articleCommentReactionDto.userDto.id}' updated reaction for comment with id '${articleCommentReactionDto.commentId}'")
        } else {
            log.trace("User with id '${articleCommentReactionDto.userDto.id}' created new reaction for comment with id '${articleCommentReactionDto.commentId}'")
        }

        ArticleCommentReactionDto createdArticleCommentReactionDto = articleCommentReactionDtoFromArticleCommentReaction(newArticleCommentReaction)

        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        //NOTIFY LOGIC...
        if (notify) {
            //notify author of comment
            //be careful not to notify same user as the action user (ex. self like, self comment etc...)
            def ac = articleCommentRepository.findById(newArticleCommentReaction.comment.id).get()
            if (createdArticleCommentReactionDto.userDto.id != ac.user.id) {
                def notificationDto = new NotificationDto(
                        null,
                        createdArticleCommentReactionDto.userDto,
                        UserService.userDtoFromUser(ac.user),//comment author
                        new NotificationTypeDto(3, "ARTICLE_COMMENT_REACTION"),
                        true,
                        createdArticleCommentReactionDto
                )
                notificationService.createNotification(notificationDto)
                log.info("Successfully created notification for comment reaction")
            }
        }

        return createdArticleCommentReactionDto

    }

    @Transactional(rollbackFor = Exception.class)
    ArticleCommentDto createNewArticleComment(ArticleCommentDto articleCommentDto, Boolean notify = true) throws Exception {
        // check article exists
        Optional<Article> optionalArticle = articleRepository.findById((Long) articleCommentDto.articleId)
        if (optionalArticle.isEmpty())
            throw new ArticleCommentException("Article with id ${articleCommentDto.articleId} was not found")

        def article = optionalArticle.get()

        User currentLoggedInUser = userRepository.findById((Long) articleCommentDto.userDto.id).get()

        //create new comment
        ArticleComment articleComment = new ArticleComment(
                article,
                [],
                currentLoggedInUser,
                articleCommentDto.comment,
                false
        )

        articleComment = articleCommentRepository.save(articleComment)
        ArticleCommentDto createdArticleCommentDto = articleCommentDtoFromArticleComment(articleComment)

        //TODO: NOT THE RIGHT PLACE FOR THE FOLLOWING ACTION
        //NOTIFY LOGIC...
        if (notify) {
            //notify author of article
            //be careful not to notify same user as the action user (ex. self like, self comment etc...)
            if (createdArticleCommentDto.userDto.id != article.user.id) {
                def notificationDto = new NotificationDto(
                        null,
                        createdArticleCommentDto.userDto,
                        UserService.userDtoFromUser(article.user),
                        new NotificationTypeDto(2, "ARTICLE_COMMENT"),
                        true,
                        createdArticleCommentDto
                )
                notificationService.createNotification(notificationDto)
                log.info("Successfully created notification for new comment")
            }
        }

        return createdArticleCommentDto
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean setIsDeletedArticle(Long articleId, Boolean isDeleted) throws Exception {
        if (articleId == null) {
            throw new ArticleException("Article id can't be empty")
        }

        Optional<Article> optionalArticle = articleRepository.findById(articleId)
        if (optionalArticle.isEmpty())
            throw new ArticleException("Article with id '${articleId}' was not found")

        def article = optionalArticle.get()
        article.isDeleted = isDeleted
        article.updatedAt = OffsetDateTime.now()
        articleRepository.save(article)
        return true
    }

    @Transactional(rollbackFor = Exception.class)
    Boolean setIsDeletedArticleComment(Long articleId, Long commentId, Boolean isDeleted) throws Exception {
        if (articleId == null) {
            throw new ArticleCommentException("Article id can't be empty")
        }

        if (commentId == null) {
            throw new ArticleCommentException("Comment id can't be empty")
        }

        Optional<ArticleComment> optionalArticleComment = articleCommentRepository.findByArticleIdAndCommentId(articleId, commentId)
        if (optionalArticleComment.isEmpty())
            throw new ArticleException("Comment with id '${commentId}' was not found for article with id '${articleId}'")

        def articleComment = optionalArticleComment.get()
        articleComment.isDeleted = isDeleted
        articleComment.updatedAt = OffsetDateTime.now()
        articleCommentRepository.save(articleComment)
        return true
    }

    ArticleDto findArticle(Long articleId) throws Exception {
        if (articleId == null) {
            throw new ArticleException("Article id can't be empty")
        }

        Optional<Article> optionalArticle = articleRepository.findById(articleId)

        return optionalArticle.isEmpty() ? null : articleDtoFromArticle(optionalArticle.get())
    }

    Page<Article> listAllArticlesOfConnectedNetworkAndIsDeleted(Long userId,
                                                                Integer page,
                                                                Integer pageSize,
                                                                String sortBy,
                                                                String order,
                                                                Boolean isDeleted = false) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        //get connected network of userId
        def connectedUserIds = userConnectionRepository.findAllConnectedUserIdsByUserId(userId)

        Page<Article> pageArticle = articleRepository.findAllByCurrentUserIdAndConnectedUserIdsAndIsDeleted(userId, connectedUserIds, isDeleted, pageable)
        return pageArticle
    }

    Page<Article> listAllArticlesByUserIdAndIsDeletedAndPublicStatusIn(Long userId,
                                                                       List<PublicStatus> publicStatusList,
                                                                       Integer page,
                                                                       Integer pageSize,
                                                                       String sortBy,
                                                                       String order,
                                                                       Boolean isDeleted = false) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))

        def publicStatusNamesList = []
        publicStatusList.each { ps ->
            publicStatusNamesList.add(ps.name())
        }
        Page<Article> pageArticle = articleRepository.findAllByUserIdAndIsDeletedAndPublicStatusIn(userId, isDeleted, publicStatusNamesList, pageable)
        return pageArticle
    }

    Page<Article> listAllArticlesByUserIdAndIsDeleted(Long userId,
                                                      Integer page,
                                                      Integer pageSize,
                                                      String sortBy,
                                                      String order,
                                                      Boolean isDeleted = false) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))
        Page<Article> pageArticle = articleRepository.findAllByUserIdAndIsDeleted(userId, isDeleted, pageable)
        return pageArticle
    }

    Page<ArticleComment> listAllArticleCommentsByArticleIdAndIsDeleted(Long articleId,
                                                                       Integer page,
                                                                       Integer pageSize,
                                                                       String sortBy,
                                                                       String order,
                                                                       Boolean isDeleted = false) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC
        if (order == "asc")
            direction = Sort.Direction.ASC
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy))
        Page<ArticleComment> pageArticleComment = articleCommentRepository.findAllByArticleIdAndIsDeleted(articleId, isDeleted, pageable)
        return pageArticleComment
    }

    ArticleCommentDto findArticleComment(Long articleId, Long commentId) throws Exception {
        if (articleId == null) {
            throw new ArticleCommentException("Article id can't be empty")
        }

        if (commentId == null) {
            throw new ArticleCommentException("Comment id can't be empty")
        }

        Optional<ArticleComment> optionalArticleComment = articleCommentRepository.findByArticleIdAndCommentId(articleId, commentId)
        return optionalArticleComment.isEmpty() ? null : articleCommentDtoFromArticleComment(optionalArticleComment.get())
    }

    ArticleReactionDto findArticleReaction(Long articleId, Long userId) throws Exception {
        if (articleId == null) {
            throw new ArticleReactionException("Article id can't be empty")
        }
        if (userId == null) {
            throw new ArticleReactionException("User id can't be empty")
        }
        Optional<ArticleReaction> optionalArticleReaction = articleReactionRepository.findByArticleIdAndUserId(articleId, userId)
        return optionalArticleReaction.isEmpty() ? null : articleReactionDtoFromArticleReaction(optionalArticleReaction.get())
    }

    ArticleCommentReactionDto findArticleCommentReaction(Long articleId, Long commentId, Long userId) throws Exception {
        if (articleId == null) {
            throw new ArticleCommentReactionException("Article id can't be empty")
        }
        if (commentId == null) {
            throw new ArticleCommentReactionException("Comment id can't be empty")
        }
        if (userId == null) {
            throw new ArticleCommentReactionException("User id can't be empty")
        }
        Optional<ArticleCommentReaction> optionalArticleCommentReaction = articleCommentReactionRepository.findByArticleIdAndCommentIdAndUserId(articleId, commentId, userId)
        return optionalArticleCommentReaction.isEmpty() ? null : articleCommentReactionDtoFromArticleCommentReaction(optionalArticleCommentReaction.get())
    }

    static List<Long> getUserIdsContainedInArticleDto(ArticleDto articleDto) throws Exception {
        def userIds = []
        //get all user entities ids contained in article
        userIds.add(articleDto.userDto.id)
        articleDto.articleComments?.each { articleCommentDto ->
            userIds += getUserIdsContainedInArticleCommentDto(articleCommentDto)
        }

        articleDto.articleReactions?.each { articleReactionDto ->
            userIds.add((Long) articleReactionDto.userDto.id)
        }

        return userIds
    }

    static List<Long> getUserIdsContainedInArticleCommentDto(ArticleCommentDto articleCommentDto) throws Exception {
        def userIds = []
        //get all user entities ids contained in article comment
        userIds.add(articleCommentDto.userDto.id)
        //also get userIds from comment reactions
        articleCommentDto.commentReactions?.each { articleCommentReactionDto ->
            userIds.add(articleCommentReactionDto.userDto.id)
        }
        return userIds
    }

    @Transactional(rollbackFor = Exception.class)
    protected List<ArticleMedia> createArticleMedias(Long articleId, List<ArticleMediaDto> articleMediaDtoList) throws Exception {
        List<ArticleMedia> articleMedias = []
        if (articleMediaDtoList != null && !articleMediaDtoList.isEmpty()) {
            Optional<Article> optionalArticle = articleRepository.findById(articleId)
            if (optionalArticle.isEmpty())
                throw new ArticleMediaException("Article with id '${articleId}' was not found")

            //first create medias
            def mediaDtoList = []
            articleMediaDtoList.each { articleMediaDto ->
                mediaDtoList.add(articleMediaDto.mediaDto)
            }
            //returned in the same order provided (so order not lost)
            def medias = mediaService.createMedias(mediaDtoList)

            //then create article medias (we now have reference ids of medias)
            articleMediaDtoList.eachWithIndex { articleMediaDto, i ->
                articleMedias.add(new ArticleMedia(
                        optionalArticle.get(),
                        medias[i],
                        articleMediaDto.order
                ))
            }

            articleMedias = articleMediaRepository.saveAll(articleMedias).toList()
        }
        return articleMedias
    }

    static ArticleMediaDto articleMediaDtoFromArticleMedia(ArticleMedia articleMedia) {
        return new ArticleMediaDto(
                articleMedia.id,
                articleMedia.article?.id,
                MediaService.mediaDtoFromMedia(articleMedia.media),
                articleMedia.order,
                articleMedia.createdAt
        )
    }

    static ArticleCommentReactionDto articleCommentReactionDtoFromArticleCommentReaction(ArticleCommentReaction articleCommentReaction, Boolean hideUserPrivateFields = false) {
        return new ArticleCommentReactionDto(
                articleCommentReaction.id,
                articleCommentReaction.user?(!hideUserPrivateFields ? UserService.userDtoFromUser(articleCommentReaction.user) : UserService.userDtoFromUserWithHiddenPrivateFields(articleCommentReaction.user)):null,
                articleCommentReaction.comment?.id,
                new ReactionDto(articleCommentReaction.reaction?.id, articleCommentReaction.reaction?.alias),
                articleCommentReaction.createdAt,
                articleCommentReaction.updatedAt
        )
    }

    static ArticleReactionDto articleReactionDtoFromArticleReaction(ArticleReaction articleReaction) {
        return new ArticleReactionDto(
                articleReaction.id,
                articleReaction.article?.id,
                articleReaction.user?UserService.userDtoFromUser(articleReaction.user):null,
                new ReactionDto(articleReaction.reaction?.id, articleReaction.reaction?.alias),
                articleReaction.createdAt,
                articleReaction.updatedAt
        )
    }

    static ArticleCommentDto articleCommentDtoFromArticleComment(ArticleComment articleComment, Boolean hideUserPrivateFields = false) {
        List<ArticleCommentReactionDto> commentReactionDtoList = []
        articleComment.commentReactions?.each { acr ->
            commentReactionDtoList.add(articleCommentReactionDtoFromArticleCommentReaction(acr, hideUserPrivateFields))
        }
        return new ArticleCommentDto(
                articleComment.id,
                articleComment.article?.id,
                articleComment.user?(!hideUserPrivateFields ? UserService.userDtoFromUser(articleComment.user) : UserService.userDtoFromUserWithHiddenPrivateFields(articleComment.user)):null,
                commentReactionDtoList,
                articleComment.body,
                articleComment.isDeleted,
                articleComment.createdAt,
                articleComment.updatedAt
        )
    }

    static ArticleDto articleDtoFromArticle(Article article) {
        List<ArticleMediaDto> mediaDtoList = []
        article.articleMedias?.each { am ->
            mediaDtoList.add(articleMediaDtoFromArticleMedia(am))
        }

        List<ArticleCommentDto> articleCommentDtoList = []
        article.articleComments?.each { ac ->
            articleCommentDtoList.add(articleCommentDtoFromArticleComment(ac))
        }

        List<ArticleReactionDto> articleReactionDtoList = []
        article.articleReactions?.each { ar ->
            articleReactionDtoList.add(articleReactionDtoFromArticleReaction(ar))
        }

        return new ArticleDto(
                article.id,
                UserService.userDtoFromUser(article.user),
                article.title,
                article.body,
                article.publicStatus,
                article.isDeleted,
                mediaDtoList,
                articleCommentDtoList,
                articleReactionDtoList,
                article.createdAt,
                article.updatedAt
        )
    }

//    static Article articleFromArticleDto(ArticleDto articleDto) {
//        return new Article(
//                (Long) articleDto.id,
//                articleDto.userDto?UserService.userFromUserDto(articleDto.userDto):null,
//                articleDto.title,
//                articleDto.body,
//                [],
//                [],
//                [],
//                articleDto.publicStatus,
//                articleDto.isDeleted,
//                articleDto.createdAt,
//                articleDto.updatedAt
//        )
//    }
}
