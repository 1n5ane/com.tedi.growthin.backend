package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.enums.PublicStatus
import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.dtos.connections.UserConnectionDto
import com.tedi.growthin.backend.dtos.users.UserDto
import com.tedi.growthin.backend.services.articles.UserArticleService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.services.users.UserConnectionService
import com.tedi.growthin.backend.services.users.UserService
import com.tedi.growthin.backend.services.validation.ValidationService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleCommentReactionException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleMediaException
import com.tedi.growthin.backend.utils.exception.validation.articles.ArticleReactionException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

//TODO: add endpoint for fetching all reactions -> frontend should map aliases
@Service
@Slf4j
class UserArticleIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserArticleService articleService

    @Autowired
    UserService userService

    @Autowired
    UserConnectionService userConnectionService

    ArticleDto createNewArticle(ArticleDto articleDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        String userIdentifier = "[userId = '${currentLoggedInUserId}', username = ${JwtService.extractUsername(userJwtToken)}]"

        articleDto.userDto = new UserDto()
        articleDto.userDto.id = currentLoggedInUserId

        //remove id if provided
        articleDto.id = null

        validationServiceMap["articleValidationService"].validate(articleDto)

        def mediaOrders = []
        articleDto.articleMedias?.each { articleMediaDto ->
            articleMediaDto.mediaDto?.userId = currentLoggedInUserId
            mediaOrders.add(articleMediaDto.order)
            validationServiceMap["articleMediaValidationService"].validate(articleMediaDto)
        }

        if (mediaOrders.size() != mediaOrders.toSet().size()) {
            //set only contains unique elements
            //so if sizes differ then -> 2 or more media contain same order number -> throw exception
            throw new ArticleMediaException("Article's media contain duplicated order numbers. Order of media must be unique")
        }
        articleDto.articleComments = []
        articleDto.articleReactions = []

        def article = articleService.createNewArticle(articleDto)
        return UserArticleService.articleDtoFromArticle(article)
    }

    ArticleCommentDto createNewArticleComment(ArticleCommentDto articleCommentDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //remove id if provided in articleCommentDto
        articleCommentDto.id = null
        articleCommentDto.userDto = new UserDto()
        articleCommentDto.userDto.id = currentLoggedInUserId
        articleCommentDto.commentReactions = []

        validationServiceMap["articleCommentValidationService"].validate(articleCommentDto)

        //check article exists
        ArticleDto articleDto = articleService.findArticle((Long) articleCommentDto.articleId)
        if (articleDto == null) {
            throw new ArticleCommentException("Article with id '${articleCommentDto.articleId}' was not found!")
        }

        //check if deleted
        if (articleDto.isDeleted) {
            throw new ForbiddenException("Article with id ${articleCommentDto.articleId} has been removed and can no further be commented!")

        }
        def userIds = UserArticleService.getUserIdsContainedInArticleDto(articleDto)
        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)
        if (articleDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleDto.userDto.id)) {
            //if not connected and author not currentLoggedInUser
            if (articleDto.publicStatus != PublicStatus.PUBLIC) {
                //if article is not public -> user can't comment
                throw new ForbiddenException("Article with id '${articleDto.id}' is not public and users are not connected. No comments can be made")
            }
        }

        // also if article is hidden
        //only author can comment it
        if (articleDto.publicStatus == PublicStatus.HIDDEN && currentLoggedInUserId != articleDto.userDto.id) {
            throw new ForbiddenException("Article with id '${articleDto.id}' is not public and no comments can be made")
        }

        def articleComment = articleService.createNewArticleComment(articleCommentDto)
        return articleComment
    }

    ArticleReactionDto createNewArticleReaction(ArticleReactionDto articleReactionDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        articleReactionDto.id = null
        articleReactionDto.userDto = new UserDto()
        articleReactionDto.userDto.id = currentLoggedInUserId

        validationServiceMap["articleReactionValidationService"].validate(articleReactionDto)

        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, (Long) articleReactionDto.articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No reactions can be made.")
        } catch (ArticleException articleException) {
            throw new ArticleReactionException(articleException.getMessage() + " No reactions can be made.")
        }

        return articleService.createNewArticleReaction(articleReactionDto)

    }

    ArticleCommentReactionDto createNewArticleCommentReaction(ArticleCommentReactionDto articleCommentReactionDto,
                                                              Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        articleCommentReactionDto.id = null
        articleCommentReactionDto.userDto = new UserDto()
        articleCommentReactionDto.userDto.id = currentLoggedInUserId

        validationServiceMap["articleCommentReactionValidationService"].validate(articleCommentReactionDto)

        //check comment exists and not deleted
        def articleCommentDto = articleService.findArticleComment(
                (Long) articleCommentReactionDto.articleId,
                (Long) articleCommentReactionDto.commentId
        )
        if (articleCommentDto == null) {
            throw new ArticleCommentReactionException("Comment with id '${articleCommentReactionDto.commentId}' was not found in article with id '${articleCommentReactionDto.articleId}'")
        }
        if (articleCommentDto.isDeleted) {
            //if comment is deleted by owner -> forbidden exception
            throw new ForbiddenException("Comment with id '${articleCommentReactionDto.commentId}' is deleted and no reactions can be made.")
        }

        //get if user is connected with article author (if can perform actions in article)
        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, (Long) articleCommentReactionDto.articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No reactions to comments can be made.")
        } catch (ArticleException articleException) {
            throw new ArticleCommentReactionException(articleException.getMessage() + " No reactions to comments can be made.")
        }

        return articleService.createNewArticleCommentReaction(articleCommentReactionDto)
    }

    Boolean deleteArticleReaction(Long articleId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        //check if user can perform this action on specific article
        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No changes to reactions can be made.")
        } catch (ArticleException articleException) {
            throw new ArticleReactionException(articleException.getMessage() + " No changes to reactions can be made.")
        }

        //all good so far
        //check reaction exists
        ArticleReactionDto articleReactionDto = articleService.findArticleReaction(articleId, currentLoggedInUserId)
        if (articleReactionDto == null) {
            throw new ArticleReactionException("User with id '${currentLoggedInUserId}' has no reaction to article with id '${articleId}'")
        }

        Boolean success = articleService.deleteArticleReaction((Long) articleReactionDto.id)
        return success
    }

    Boolean deleteArticleCommentReaction(Long articleId, Long commentId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check comment exists and not deleted
        def articleCommentDto = articleService.findArticleComment(articleId, commentId)
        if (articleCommentDto == null) {
            throw new ArticleCommentReactionException("Comment with id '${commentId}' was not found in article with id '${articleId}'")
        }
        if (articleCommentDto.isDeleted) {
            //if comment is deleted by owner -> forbidden exception
            throw new ForbiddenException("Comment with id '${commentId}' is deleted and no changes to reactions can be made.")
        }

        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No changes to comment reactions can be made.")
        } catch (ArticleException articleException) {
            throw new ArticleReactionException(articleException.getMessage() + " No changes to comment reactions can be made.")
        }

        //all good so far
        //check reaction exists
        ArticleCommentReactionDto articleCommentReactionDto = articleService.findArticleCommentReaction(articleId, commentId, currentLoggedInUserId)
        if (articleCommentReactionDto == null) {
            throw new ArticleCommentReactionException("User with id '${currentLoggedInUserId}' has no reaction to comment with id '${commentId}' in article with id '${articleId}'")
        }

        Boolean success = articleService.deleteArticleCommentReaction((Long) articleCommentReactionDto.id)
        return success

    }

    Boolean setIsDeletedArticle(Long articleId, Boolean isDeleted, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        if (isDeleted == null)
            throw new ArticleException("Is deleted flag is of type boolean and can't be null")

        if (articleId == null) {
            throw new ArticleException("Article id can't be empty")
        }

        //fetch article
        ArticleDto articleDto = articleService.findArticle(articleId)
        if (articleDto == null) {
            throw new ArticleException("Article with id '${articleId}' was not found")
        }

        //check if article owned by currentLoggeInUser
        if (articleDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("Article with id '${articleId}' was not created by you")
        }

        Boolean success = true
        if (articleDto.isDeleted != isDeleted)
            success = articleService.setIsDeletedArticle(articleId, isDeleted)

        return success
    }

    Boolean setIsDeletedArticleComment(Long articleId, Long commentId, Boolean isDeleted, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        if (isDeleted == null)
            throw new ArticleCommentException("Is deleted flag is of type boolean and can't be null")
        if (articleId == null) {
            throw new ArticleCommentException("Article id can't be empty")
        }
        if (commentId == null) {
            throw new ArticleCommentException("Comment id can't be empty")
        }

        //fetch article
        ArticleDto articleDto = articleService.findArticle(articleId)
        if (articleDto == null) {
            throw new ArticleException("Article with id '${articleId}' was not found")
        }

        if (articleDto.isDeleted) {
            //if article is deleted by owner -> forbidden exception
            throw new ForbiddenException("Article with id '${articleId}' is deleted and no further changes to comments can be applied")
        }

        def userIds = UserArticleService.getUserIdsContainedInArticleDto(articleDto)
        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)

        if (articleDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleDto.userDto.id)) {
            //if not connected
            if (articleDto.publicStatus != PublicStatus.PUBLIC) {
                //if article is not public -> user can't update comments (they can't comment either)
                throw new ForbiddenException("Article with id '${articleDto.id}' is not public and users are not connected. No changes to comments can be applied")
            }
        }

        //if article is hidden and currentLoggedInUser not owner of article
        if (articleDto.publicStatus == PublicStatus.HIDDEN && articleDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("Article with id '${articleId}' is not public and no changes to comments can be applied")
        }

        //fetch article comment
        ArticleCommentDto articleCommentDto = articleService.findArticleComment(articleId, commentId)

        if (articleCommentDto == null) {
            throw new ArticleCommentException("Comment with id '${commentId}' was not found for article with id '${articleId}'")
        }

        //check if currentLoggedInUser is owner of comment
        if (articleCommentDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("Comment with id '${commentId}' was not created by you")
        }

        //all good so far -> set is deleted (if not already deleted)
        def success = true
        if (articleCommentDto.isDeleted != isDeleted)
            success = articleService.setIsDeletedArticleComment(articleId, commentId, isDeleted)

        return success
    }

    private void checkIfAuthorizedToAccessArticle(Long currentLoggedInUserId, Long articleId) throws Exception {
        //check article exists
        def articleDto = articleService.findArticle((Long) articleId)
        if (articleDto == null) {
            throw new ArticleException("Article with id '${articleId}' was not found.")
        }

        if (articleDto.isDeleted) {
            //if article is deleted by owner -> forbidden exception
            throw new ForbiddenException("Article with id '${articleId}' is deleted.")
        }

        def userConnection = new UserConnectionDto()
        userConnection.userId = currentLoggedInUserId
        userConnection.connectedUserId = articleDto.userDto.id
        def isConnected = userConnectionService.checkUserConnectionExists(userConnection)

        if (articleDto.userDto.id != currentLoggedInUserId && !isConnected) {
            //if not connected
            if (articleDto.publicStatus != PublicStatus.PUBLIC) {
                //if article is not public -> user can't update comments (they can't comment either)
                throw new ForbiddenException("Article with id '${articleDto.id}' is not public and users are not connected.")
            }
        }

        //if article is hidden and currentLoggedInUser not owner of article
        if (articleDto.publicStatus == PublicStatus.HIDDEN && articleDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("Article with id '${articleDto.id}' is not public.")
        }
    }

    ArticleDto findUserArticle(Long articleId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        if (articleId == null) {
            throw new ArticleException("Article id can't be empty")
        }

        def articleDto = articleService.findArticle(articleId)
        if (articleDto == null)
            return null

        if (articleDto.isDeleted) {
            throw new ForbiddenException("Article with id '${articleId}' is deleted and can't be viewed")
        }


        def userIds = UserArticleService.getUserIdsContainedInArticleDto(articleDto)

        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)

        //check if hidden and not currentLoggedInUser requestsArticle
        if (articleDto.publicStatus == PublicStatus.HIDDEN && currentLoggedInUserId != articleDto.userDto.id) {
            throw new ForbiddenException("Article with id '${articleId}' is hidden and can't be viewed")
        }

        if (articleDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleDto.userDto.id)) {
            //if not currentLoggedInUser owner of article and not connected with article owner

            //check if can view article
            if (articleDto.publicStatus != PublicStatus.PUBLIC) {
                throw new ForbiddenException("Article is not public and are not connected with article owner")
            }
            articleDto.userDto = UserDto.hidePrivateFields(articleDto.userDto)
        }

        //hide user entity fields for non connected users in comments
        articleDto.articleComments?.eachWithIndex { articleCommentDto, i ->
            if (articleCommentDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleCommentDto.userDto.id)) {
                //if not currentLoggedInUser owner of comment and not connected with comment owner
                articleDto.articleComments[i].userDto = UserDto.hidePrivateFields(articleCommentDto.userDto)
            }

            articleCommentDto.commentReactions?.eachWithIndex { articleCommentReactionDto, j ->
                //also restrict user fields on user comment reactions
                if (articleCommentReactionDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleCommentReactionDto.userDto.id)) {
                    articleDto.articleComments[i].commentReactions[j].userDto = UserDto.hidePrivateFields(articleCommentReactionDto.userDto)
                }
            }
        }

        return articleDto
    }

}
