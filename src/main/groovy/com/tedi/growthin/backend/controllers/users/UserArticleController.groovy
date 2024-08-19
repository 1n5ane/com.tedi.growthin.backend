package com.tedi.growthin.backend.controllers.users

import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.services.UserArticleIntegrationService
import com.tedi.growthin.backend.services.jwt.JwtService
import com.tedi.growthin.backend.utils.exception.ForbiddenException
import com.tedi.growthin.backend.utils.exception.validation.ValidationException
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/article")
@Slf4j

//TODO: TEST EVERYTHING
class UserArticleController {
    @Autowired
    JwtService jwtService

    @Autowired
    UserArticleIntegrationService userArticleIntegrationService

    @PostMapping(value = ["/", ""], produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewArticle(@RequestBody ArticleDto articleDto,
                         Authentication authentication) {

        def response = [
                "success": true,
                "article": null,
                "error"  : ""
        ]

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def createdArticleDto = userArticleIntegrationService.createNewArticle(articleDto, authentication)
            response["article"] = createdArticleDto
            log.info("${userIdentifier} Successfully created new article with id '${createdArticleDto.id}'")
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to create a new article: " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to create a new article: ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = "/{articleId}/comment", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewArticleComment(@PathVariable("articleId") Long articleId,
                                @RequestBody ArticleCommentDto articleCommentDto,
                                Authentication authentication) {
        def response = [
                "success"       : true,
                "articleComment": null,
                "error"         : ""
        ]

        try {
            articleId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id '${articleId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        articleCommentDto.articleId = articleId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def createdArticleCommentDto = userArticleIntegrationService.createNewArticleComment(articleCommentDto, authentication)
            response["articleComment"] = createdArticleCommentDto
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to add comment to article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to add comment to article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to add comment to article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @PostMapping(value = "/{articleId}/comment/{commentId}/reaction", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewArticleCommentReaction(@PathVariable("articleId") Long articleId,
                                        @PathVariable("commentId") Long commentId,
                                        @RequestBody ArticleCommentReactionDto articleCommentReactionDto,
                                        Authentication authentication) {
        //if user has already reacted to comment -> update reaction
        def response = [
                "success"               : true,
                "articleCommentReaction": null,
                "error"                 : ""
        ]

        try {
            articleId.toLong()
            commentId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id or comment id".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        articleCommentReactionDto.articleId = articleId.toLong()
        articleCommentReactionDto.commentId = commentId.toLong()

        try {
            def createdArticleCommentReaction = userArticleIntegrationService.createNewArticleCommentReaction(articleCommentReactionDto, authentication)
            response["articleCommentReaction"] = createdArticleCommentReaction
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to react to comment with id '${commentId}' in article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to react to comment with id '${commentId}' in article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to react to comment with id '${commentId}' in article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }

    @PostMapping(value = "/{articleId}/reaction", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def createNewArticleReaction(@PathVariable("articleId") Long articleId,
                                 @RequestBody ArticleReactionDto articleReactionDto,
                                 Authentication authentication) {
        //if user has already reacted -> update reaction
        def response = [
                "success"        : true,
                "articleReaction": null,
                "error"          : ""
        ]

        try {
            articleId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id '${articleId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        articleReactionDto.articleId = articleId.toLong()

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def createdArticleReaction = userArticleIntegrationService.createNewArticleReaction(articleReactionDto, authentication)
            response["articleReaction"] = createdArticleReaction
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to react to article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to react to article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to react to article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @DeleteMapping(value = "/{articleId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def deleteArticle(@PathVariable("articleId") String articleId,
                      Authentication authentication) {

        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            articleId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id '${articleId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            Boolean success = userArticleIntegrationService.setIsDeletedArticle(articleId.toLong(), true, authentication)
            response["success"] = success
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to delete article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to delete article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to delete article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @DeleteMapping(value = "/{articleId}/comment/{commentId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def deleteArticleComment(@PathVariable("articleId") String articleId,
                             @PathVariable("commentId") String commentId,
                             Authentication authentication) {


        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            articleId.toLong()
            commentId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id or comment id".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            Boolean success = userArticleIntegrationService.setIsDeletedArticleComment(articleId.toLong(), commentId.toLong(), true, authentication)
            response["success"] = success
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to delete comment with id '${commentId}' of article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to delete comment with id '${commentId}' of article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to delete comment with id '${commentId}' of article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)

    }

    @DeleteMapping(value = "/{articleId}/reaction", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def deleteArticleReaction(@PathVariable("articleId") Long articleId, Authentication authentication) {
        //delete article reaction if exists
        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            articleId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id '${articleId}'".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            Boolean success = userArticleIntegrationService.deleteArticleReaction(articleId, authentication)
            response["success"] = success
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to delete reaction from article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to delete reaction from article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to delete reaction from article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @DeleteMapping(value = "/{articleId}/comment/{commentId}/reaction", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def deleteArticleCommentReaction(@PathVariable("articleId") Long articleId,
                                     @PathVariable("commentId") Long commentId,
                                     Authentication authentication) {

        def response = [
                "success": true,
                "error"  : ""
        ]

        try {
            articleId.toLong()
            commentId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id or comment id".toString()
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            Boolean success = userArticleIntegrationService.deleteArticleCommentReaction(articleId.toLong(), commentId.toLong(), authentication)
            response["success"] = success
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to delete reaction to comment with id '${commentId}' of article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to delete reaction to comment with id '${commentId}' of article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to delete reaction to comment with id '${commentId}' of article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    @GetMapping(value = "/{articleId}", produces = "application/json;charset=UTF-8")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseBody
    def getArticleById(@PathVariable("articleId") String articleId, Authentication authentication) {
        def response = [
                "success": true,
                "article": null,
                "error"  : ""
        ]

        try {
            articleId.toLong()
        } catch (NumberFormatException ignored) {
            response["success"] = false
            response["error"] = "Invalid article id"
            return new ResponseEntity<>(response, HttpStatus.OK)
        }

        def jwtToken = (Jwt) authentication.getCredentials()
        String userIdentifier = "[userId = '${JwtService.extractAppUserId(jwtToken)}', username = ${JwtService.extractUsername(jwtToken)}]"

        try {
            def articleDto = userArticleIntegrationService.findUserArticle(articleId.toLong(), authentication)
            if (articleDto == null) {
                response["success"] = false
                response["error"] = "Article was not found"
            }
            response["article"] = articleDto
        } catch (ForbiddenException forbiddenException) {
            log.trace("${userIdentifier} Forbidden to get user article with id '${articleId}': ${forbiddenException.getMessage()}")
            response["success"] = false
            response["error"] = "Access is forbidden: ${forbiddenException.getMessage()}".toString()
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN)
        } catch (ValidationException validationException) {
            log.trace("${userIdentifier} Failed to get user article with id '${articleId}': " + validationException.getMessage())
            response["success"] = false
            response["error"] = validationException.getMessage()
        } catch (Exception exception) {
            log.error("${userIdentifier} Failed to get user article with id '${articleId}': ${exception.getMessage()}")
            response["success"] = false
            response["error"] = "An error occured! Please try again later"
        }

        return new ResponseEntity<>(response, HttpStatus.OK)
    }

    //findAllByIdList

    //findAllByUserId

    //findAll (either public or connected network)

    //update article (+ PUBLIC STATUSES)

    //update article comment (+ PUBLIC STATUSES)
}
