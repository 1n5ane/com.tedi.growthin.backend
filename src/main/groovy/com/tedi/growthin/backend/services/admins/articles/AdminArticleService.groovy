package com.tedi.growthin.backend.services.admins.articles

import com.tedi.growthin.backend.domains.articles.Article
import com.tedi.growthin.backend.domains.articles.ArticleComment
import com.tedi.growthin.backend.domains.articles.ArticleCommentReaction
import com.tedi.growthin.backend.domains.articles.ArticleReaction
import com.tedi.growthin.backend.dtos.articles.ArticleCommentDto
import com.tedi.growthin.backend.dtos.articles.ArticleCommentReactionDto
import com.tedi.growthin.backend.dtos.articles.ArticleDto
import com.tedi.growthin.backend.dtos.articles.ArticleReactionDto
import com.tedi.growthin.backend.services.articles.UserArticleService
import org.springframework.stereotype.Service

@Service
class AdminArticleService extends UserArticleService {


    List<ArticleDto> listAllByUserIds(List<Long> userIds) throws Exception {
        List<Article> articleList = this.articleRepository.findAllByUserIds(userIds)
        def articleDtoList = []
        articleList.each { a ->
            articleDtoList.add(articleDtoFromArticle(a))
        }
        return articleDtoList
    }

    List<ArticleReactionDto> listAllArticleReactionsByUserIds(List<Long> userIds) throws Exception {
        List<ArticleReaction> articleReactionList = this.articleReactionRepository.findAllByUserIds(userIds)
        def articleReactionDtoList = []
        articleReactionList.each { ar ->
            articleReactionDtoList.add(articleReactionDtoFromArticleReaction(ar))
        }
        return articleReactionDtoList
    }

    List<ArticleCommentDto> listAllCommentsByUserIds(List<Long> userIds) throws Exception {
        List<ArticleComment> articleCommentList = this.articleCommentRepository.findAllByUserIds(userIds)
        def articleCommentDtoList = []
        articleCommentList.each { ac ->
            articleCommentDtoList.add(articleCommentDtoFromArticleComment(ac))
        }
        return articleCommentDtoList
    }

    List<ArticleCommentReactionDto> listAllArticleCommentReactionsByUserIds(List<Long> userIds) throws Exception {
        List<ArticleCommentReaction> articleCommentReactionList = this.articleCommentReactionRepository.findAllByUserIds(userIds)
        def articleCommentReactionDtoList = []
        articleCommentReactionList.each { acr ->
            articleCommentReactionDtoList.add(articleCommentReactionDtoFromArticleCommentReaction(acr))
        }
        return articleCommentReactionDtoList
    }
}
