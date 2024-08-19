package com.tedi.growthin.backend.repositories.articles

import com.tedi.growthin.backend.domains.articles.ArticleCommentReaction
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleCommentReactionRepository extends PagingAndSortingRepository<ArticleCommentReaction, Long>, CrudRepository<ArticleCommentReaction, Long> {

    @Query("select acr from ArticleCommentReaction acr where acr.user.id = :userId and acr.comment.id = :commentId and acr.comment.article.id = :articleId")
    Optional<ArticleCommentReaction> findByArticleIdAndCommentIdAndUserId(@Param("articleId") Long articleId, @Param("commentId") Long commentId, @Param("userId") Long userId)

}