package com.tedi.growthin.backend.services

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.enums.PublicStatus
import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentListDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleListDto
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
import com.tedi.growthin.backend.utils.exception.validation.paging.PagingArgumentException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
@Slf4j
class UserArticleIntegrationService {

    @Autowired
    Map<String, ValidationService> validationServiceMap

    @Autowired
    UserArticleService userArticleService

    @Autowired
    UserService userService

    @Autowired
    UserConnectionService userConnectionService

    ArticleDto createNewArticle(ArticleDto articleDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
//        String userIdentifier = "[userId = '${currentLoggedInUserId}', username = ${JwtService.extractUsername(userJwtToken)}]"

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

        def createdArticleDto = userArticleService.createNewArticle(articleDto)
        return createdArticleDto
    }

    ArticleDto updateArticle(ArticleDto articleDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        articleDto.userDto = new UserDto()
        articleDto.userDto.id = currentLoggedInUserId


        //find article and check if author is currentLoggedInUser
        def toBeUpdatedArticleDto = userArticleService.findArticle((Long) articleDto.id)
        if (toBeUpdatedArticleDto == null) {
            throw new ArticleException("Article with id '${articleDto.id}' was not found")
        }

        if (toBeUpdatedArticleDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("You are not the author of the article. Can't update article.")
        }

        //currentLoggedInUser is author of article (ok sofar)
        //No delete can be done from here -> there is endpoint for that
        articleDto.isDeleted = null
        articleDto.articleReactions = [] // can't update that
        articleDto.articleComments = [] // can't update that either

        //null fields won't be updated and will be left as is
        //to remove for example title -> provide empty string
        //to remove articleMedias -> provide empty list (null means no update)

        //validate update
        validationServiceMap["articleUpdateValidationService"].validate(articleDto)

        //TODO: updating media of article not implemented (add/remove/change order)
        //when implement remove the following line
        articleDto.articleMedias = null

        def updatedArticleDto = userArticleService.updateArticle(articleDto)
        //hide private fields of not connected users (with currentLoggedInUser)
        if (updatedArticleDto != null) {
            //if indeed updated
            updatedArticleDto = this.hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, updatedArticleDto)
        }
        return updatedArticleDto
    }

    ArticleCommentDto updateArticleComment(ArticleCommentDto articleCommentDto, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        articleCommentDto.userDto = new UserDto()
        articleCommentDto.userDto.id = currentLoggedInUserId
        articleCommentDto.commentReactions = []


        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, (Long) articleCommentDto.articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No comment updates can be made")
        } catch (ArticleException articleException) {
            throw new ArticleCommentException(articleException.getMessage() + " No comment updates can be made")
        }

        //validate
        validationServiceMap["articleCommentValidationService"].validate(articleCommentDto)

        //check if currentLoggedInUser is author of particular comment
        def fetchedArticleComment = userArticleService.findArticleComment(
                (Long) articleCommentDto.articleId,
                (Long) articleCommentDto.id
        )

        if (fetchedArticleComment == null) {
            throw new ArticleCommentException("Comment with id '${articleCommentDto.id}' was not found")
        }

        //user not author of article -> can't update comment
        if (fetchedArticleComment.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("This comment was not made by you. Comment can't be updated")
        }

        //if comment is deleted -> can't be update either
        if (fetchedArticleComment.isDeleted) {
            throw new ForbiddenException("This comment is deleted and can't be updated")
        }

        def updatedArticleComment = userArticleService.updateArticleComment(articleCommentDto)
        if (updatedArticleComment != null) {
            //hide user reaction private fields
            updatedArticleComment = hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, updatedArticleComment)
        }

        return updatedArticleComment
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

        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, (Long) articleCommentDto.articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No comments can be made")
        } catch (ArticleException articleException) {
            throw new ArticleCommentException(articleException.getMessage() + " No comments can be made")
        }

        def articleComment = userArticleService.createNewArticleComment(articleCommentDto)
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

        return userArticleService.createNewArticleReaction(articleReactionDto)

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
        def articleCommentDto = userArticleService.findArticleComment(
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

        return userArticleService.createNewArticleCommentReaction(articleCommentReactionDto)
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
        ArticleReactionDto articleReactionDto = userArticleService.findArticleReaction(articleId, currentLoggedInUserId)
        if (articleReactionDto == null) {
            throw new ArticleReactionException("User with id '${currentLoggedInUserId}' has no reaction to article with id '${articleId}'")
        }

        Boolean success = userArticleService.deleteArticleReaction((Long) articleReactionDto.id)
        return success
    }

    Boolean deleteArticleCommentReaction(Long articleId, Long commentId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check comment exists and not deleted
        def articleCommentDto = userArticleService.findArticleComment(articleId, commentId)
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
            throw new ArticleCommentException(articleException.getMessage() + " No changes to comment reactions can be made.")
        }

        //all good so far
        //check reaction exists
        ArticleCommentReactionDto articleCommentReactionDto = userArticleService.findArticleCommentReaction(articleId, commentId, currentLoggedInUserId)
        if (articleCommentReactionDto == null) {
            throw new ArticleCommentReactionException("User with id '${currentLoggedInUserId}' has no reaction to comment with id '${commentId}' in article with id '${articleId}'")
        }

        Boolean success = userArticleService.deleteArticleCommentReaction((Long) articleCommentReactionDto.id)
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
        ArticleDto articleDto = userArticleService.findArticle(articleId)
        if (articleDto == null) {
            throw new ArticleException("Article with id '${articleId}' was not found")
        }

        //check if article owned by currentLoggeInUser
        if (articleDto.userDto.id != currentLoggedInUserId) {
            throw new ForbiddenException("Article with id '${articleId}' was not created by you")
        }

        Boolean success = true
        if (articleDto.isDeleted != isDeleted)
            success = userArticleService.setIsDeletedArticle(articleId, isDeleted)

        return success
    }

    Boolean setIsDeletedArticleComment(Long articleId, Long commentId, Boolean isDeleted, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)
        if (isDeleted == null)
            throw new ArticleCommentException("Is deleted flag is of type boolean and can't be null")
        if (commentId == null) {
            throw new ArticleCommentException("Comment id can't be empty")
        }

        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " No changes to comments can be made")
        } catch (ArticleException articleException) {
            throw new ArticleCommentException(articleException.getMessage() + " No changes to comments can be made")
        }

        //fetch article comment
        ArticleCommentDto articleCommentDto = userArticleService.findArticleComment(articleId, commentId)
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
            success = userArticleService.setIsDeletedArticleComment(articleId, commentId, isDeleted)

        return success
    }


    ArticleDto findUserArticle(Long articleId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        ArticleDto articleDto
        try {
            articleDto = this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " Article can't be viewed.")
        } catch (ArticleException ignored) {
            return null
        }

        articleDto = this.hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, articleDto)
        return articleDto
    }

    Long countUserArticleComments(Long articleId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        Long count
        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " Article can't be viewed.")
        } catch (ArticleException ignored) {
            return null
        }

        return userArticleService.countArticleComments(articleId)
    }

    ArticleListDto findAllArticles(Integer page,
                                   Integer pageSize,
                                   String sortBy,
                                   String order,
                                   Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)


        //list all articles
        //- articles with status CONNECTED_NETWORK (for users connected with currentLoggedInUser)
        //- all PUBLIC articles
        //- hidden articles of currentLoggedInUser
        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt, updatedAt]")

        Page<Article> articlePage = userArticleService.listAllArticlesOfConnectedNetworkAndIsDeleted(
                currentLoggedInUserId,
                page,
                pageSize,
                sortBy,
                order,
                false)

        ArticleListDto articleListDto = new ArticleListDto()
        articleListDto.totalPages = articlePage.totalPages

        if (articlePage.isEmpty()) {
            return articleListDto
        }

        List<Article> articleList = articlePage.getContent()
        def articleDtos = []
        articleList.each { a ->
            articleDtos.add(hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, UserArticleService.articleDtoFromArticle(a)))
        }
        articleListDto.articles = articleDtos
        return articleListDto
    }


    ArticleListDto findAllUserArticles(Long userId,
                                       Integer page,
                                       Integer pageSize,
                                       String sortBy,
                                       String order,
                                       Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check if userId exists
        if (userService.getUserById(userId) == null) {
            throw new ArticleException("User with id '${userId}' was not found")
        }

        //list all articles of user
        //if user not connected with currentLoggedInUser
        //list all PUBLIC articles (and not deleted)
        //otherwise list all (not deleted) except HIDDEN
        //only list hidden if currentLoggedInUserId == userId

        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "createdAt", "updatedAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt, updatedAt]")


        Page<Article> articlePage

        if (currentLoggedInUserId == userId) {
            //if user lists all his own articles
            //fetch PUBLIC + CONNECTED_NETWORK + HIDDEN (and not deleted)
            articlePage = userArticleService.listAllArticlesByUserIdAndIsDeleted(userId, page, pageSize, sortBy, order, false)
        } else {
            //check if users are connected (currentLoggedInUser with userId)
            def userConnection = new UserConnectionDto(null, currentLoggedInUserId, userId)
            def isConnected = userConnectionService.checkUserConnectionExists(userConnection)
            def publicStatusList
            if (isConnected) {
                //fetch PUBLIC + CONNECTED_NETWORK (and of course not deleted)
                publicStatusList = [PublicStatus.PUBLIC, PublicStatus.CONNECTED_NETWORK]
            } else {
                //fetch PUBLIC (and again not deleted) as users are not connected
                publicStatusList = [PublicStatus.PUBLIC]
            }

            articlePage = userArticleService.listAllArticlesByUserIdAndIsDeletedAndPublicStatusIn(
                    userId,
                    publicStatusList,
                    page,
                    pageSize,
                    sortBy,
                    order, false
            )
        }

        ArticleListDto articleListDto = new ArticleListDto()
        articleListDto.totalPages = articlePage.totalPages

        if (articlePage.isEmpty()) {
            return articleListDto
        }

        List<Article> articleList = articlePage.getContent()
        def articleDtos = []
        articleList.each { a ->
            articleDtos.add(hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, UserArticleService.articleDtoFromArticle(a)))
        }
        articleListDto.articles = articleDtos
        return articleListDto
    }

    ArticleCommentDto findArticleComment(Long articleId, Long commentId, Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        //check if user is authorized to access article
        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " Comment can't be viewed.")
        } catch (ArticleException articleException) {
            throw new ArticleCommentException(articleException.getMessage() + " Comment can't be viewed.")
        }

        ArticleCommentDto articleCommentDto = userArticleService.findArticleComment(articleId, commentId)
        if(articleCommentDto == null){
            throw new ArticleCommentException("Comment was not found")
        }

        return hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, articleCommentDto)
    }

    ArticleCommentListDto findAllUserArticleComments(Long articleId,
                                                     Integer page,
                                                     Integer pageSize,
                                                     String sortBy,
                                                     String order,
                                                     Authentication authentication) throws Exception {
        def userJwtToken = (Jwt) authentication.getCredentials()
        Long currentLoggedInUserId = JwtService.extractAppUserId(userJwtToken)

        validationServiceMap["pagingArgumentsValidationService"].validate([
                "page"    : page,
                "pageSize": pageSize,
                "order"   : order
        ])

        sortBy = sortBy.trim()
        if (!["id", "createdAt"].contains(sortBy))
            throw new PagingArgumentException("SortBy can only be one of [id, createdAt]")

        //check if user is authorized to access article
        try {
            this.checkIfAuthorizedToAccessArticle(currentLoggedInUserId, articleId)
        } catch (ForbiddenException forbiddenException) {
            throw new ForbiddenException(forbiddenException.getMessage() + " Comments can't be viewed.")
        } catch (ArticleException articleException) {
            throw new ArticleCommentException(articleException.getMessage() + " Comments can't be viewed.")
        }

        Page<ArticleComment> articlecCommentPage = userArticleService.listAllArticleCommentsByArticleIdAndIsDeleted(articleId, page, pageSize, sortBy, order, false)

        ArticleCommentListDto articleCommentListDto = new ArticleCommentListDto()
        articleCommentListDto.totalPages = articlecCommentPage.totalPages

        if (articlecCommentPage.isEmpty()) {
            return articleCommentListDto
        }

        List<ArticleComment> articleCommentList = articlecCommentPage.getContent()
        def articleCommentsDtos = []
        articleCommentList.each { ac ->
            articleCommentsDtos.add(hidePrivateUserFieldsOfNotConnectedUsers(currentLoggedInUserId, UserArticleService.articleCommentDtoFromArticleComment(ac)))
        }
        articleCommentListDto.articleComments = articleCommentsDtos
        return articleCommentListDto
    }

    //return artilcleDto entity or throw ForbiddenException
    private ArticleDto checkIfAuthorizedToAccessArticle(Long currentLoggedInUserId, Long articleId) throws Exception {
        //check article exists
        def articleDto = userArticleService.findArticle((Long) articleId)
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
        return articleDto
    }

    ArticleCommentDto hidePrivateUserFieldsOfNotConnectedUsers(Long currentLoggedInUserId, ArticleCommentDto articleCommentDto) throws Exception {
        def userIds = UserArticleService.getUserIdsContainedInArticleCommentDto(articleCommentDto)
        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)

        //if not connected with comment author hide fields
        def commentAuthorId = articleCommentDto.userDto.id
        if (commentAuthorId != currentLoggedInUserId && !connectedUserIds.contains(commentAuthorId))
            articleCommentDto.userDto = UserDto.hidePrivateFields(articleCommentDto.userDto)

        //hide user private fields in comment reactions
        articleCommentDto.commentReactions?.eachWithIndex { articleCommentReactionDto, i ->
            def reactionUserId = articleCommentReactionDto.userDto.id
            if (reactionUserId != currentLoggedInUserId && !connectedUserIds.contains(reactionUserId)) {
                articleCommentDto.commentReactions[i].userDto = UserDto.hidePrivateFields(articleCommentReactionDto.userDto)
            }
        }
        return articleCommentDto
    }

    ArticleDto hidePrivateUserFieldsOfNotConnectedUsers(Long currentLoggedInUserId, ArticleDto articleDto) throws Exception {
        def userIds = UserArticleService.getUserIdsContainedInArticleDto(articleDto)
        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)

        //if not connected with article author hide fields
        if (articleDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleDto.userDto.id))
            articleDto.userDto = UserDto.hidePrivateFields(articleDto.userDto)


        //hide user entity fields for non connected users in comments
        articleDto.articleComments?.eachWithIndex { articleCommentDto, i ->
            def commentAuthorId = articleCommentDto.userDto.id
            if (commentAuthorId != currentLoggedInUserId && !connectedUserIds.contains(commentAuthorId)) {
                //if not currentLoggedInUser author of comment and not connected with comment author
                articleDto.articleComments[i].userDto = UserDto.hidePrivateFields(articleCommentDto.userDto)
            }

            articleCommentDto.commentReactions?.eachWithIndex { articleCommentReactionDto, j ->
                //also restrict user fields on user comment reactions
                if (articleCommentReactionDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleCommentReactionDto.userDto.id)) {
                    articleDto.articleComments[i].commentReactions[j].userDto = UserDto.hidePrivateFields(articleCommentReactionDto.userDto)
                }
            }
        }

        //hide user entity fields for non connected users in article reactions
        articleDto.articleReactions?.eachWithIndex { articleReactionDto, i ->
            def authorUserId = articleReactionDto.userDto.id
            if (authorUserId != currentLoggedInUserId && !connectedUserIds.contains(authorUserId))
                articleDto.articleReactions[i].userDto = UserDto.hidePrivateFields(articleReactionDto.userDto)
        }

        return articleDto
    }


    List<ArticleCommentReactionDto> hidePrivateUserFieldsOfCommentReactionsOfNotConnectedUsers(Long currentLoggedInUserId, List<ArticleCommentReactionDto> articleCommentReactions) throws Exception {
        if (articleCommentReactions.isEmpty())
            return []

        def userIds = []
        def articleCommentReactionDtos = []
        articleCommentReactions.each { articleCommentReactionDto ->
            userIds.add((Long) articleCommentReactionDto.userDto.id)
        }

        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)
        articleCommentReactions.each { articleCommentReactionDto ->
            if (articleCommentReactionDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleCommentReactionDto.userDto.id)) {
                //if not connected (or user not the reaction owner)
                //hide private fields
                articleCommentReactionDto.userDto = UserDto.hidePrivateFields(articleCommentReactionDto.userDto)
            }
            articleCommentReactionDtos.add(articleCommentReactionDto)
        }

        return articleCommentReactionDtos
    }

    List<ArticleReactionDto> hidePrivateUserFieldsOfArticleReactionsOfNotConnectedUsers(Long currentLoggedInUserId, List<ArticleReactionDto> articleReactions) throws Exception {
        if (articleReactions.isEmpty())
            return []

        def userIds = []
        def articleReactionDtos = []
        articleReactions.each { articleReactionDto ->
            userIds.add((Long) articleReactionDto.userDto.id)
        }

        def connectedUserIds = userConnectionService.getConnectedUserIdsFromIdList(currentLoggedInUserId, userIds)
        articleReactions.each { articleReactionDto ->
            if (articleReactionDto.userDto.id != currentLoggedInUserId && !connectedUserIds.contains(articleReactionDto.userDto.id)) {
                //if not connected (or user not the reaction owner)
                //hide private fields
                articleReactionDto.userDto = UserDto.hidePrivateFields(articleReactionDto.userDto)
            }
            articleReactionDtos.add(articleReactionDto)
        }

        return articleReactionDtos
    }

}
