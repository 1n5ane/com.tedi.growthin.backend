package com.tedi.growthin.backend.repositories.articles

import com.tedi.growthin.backend.domains.articles.ArticleMedia
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ArticleMediaRepository extends PagingAndSortingRepository<ArticleMedia, Long>, CrudRepository<ArticleMedia, Long> {

}