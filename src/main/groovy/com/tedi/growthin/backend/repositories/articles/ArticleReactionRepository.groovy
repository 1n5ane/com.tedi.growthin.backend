package com.tedi.growthin.backend.repositories.articles

import com.tedi.growthin.backend.domains.articles.ArticleReaction
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleReactionRepository extends PagingAndSortingRepository<ArticleReaction, Long>, CrudRepository<ArticleReaction, Long> {

    @Query("select ar from ArticleReaction ar where ar.article.id = :articleId and ar.user.id = :userId")
    Optional<ArticleReaction> findByArticleIdAndUserId(@Param("articleId") Long articleId,@Param("userId") Long userId)
}