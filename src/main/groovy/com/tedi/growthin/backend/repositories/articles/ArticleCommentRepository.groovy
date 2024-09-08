package com.tedi.growthin.backend.repositories.articles

import com.tedi.growthin.backend.domains.articles.ArticleComment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleCommentRepository extends PagingAndSortingRepository<ArticleComment, Long>, CrudRepository<ArticleComment, Long> {

    @Query("select ac from ArticleComment ac where ac.article.id = :articleId and ac.id = :commentId")
    Optional<ArticleComment> findByArticleIdAndCommentId(@Param("articleId") Long articleId, @Param("commentId") Long commentId)

    @Query("select ac from ArticleComment ac where ac.article.id = :articleId and ac.isDeleted = :isDeleted")
    Page<ArticleComment> findAllByArticleIdAndIsDeleted(@Param("articleId") Long articleId, @Param("isDeleted") Boolean isDeleted, Pageable pageable)

    @Query("select ac from ArticleComment ac where ac.article.id = :articleId")
    Page<ArticleComment> findAllByArticleId(@Param("articleId") Long articleId, Pageable pageable)

    //count non deleted comments
    @Query("select count(1) from ArticleComment ac where ac.article.id = :articleId and ac.isDeleted = false")
    Long countArticleComments(@Param("articleId") Long articleId)

}