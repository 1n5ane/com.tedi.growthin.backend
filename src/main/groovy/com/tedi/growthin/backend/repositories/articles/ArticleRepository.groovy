package com.tedi.growthin.backend.repositories.articles

import com.tedi.growthin.backend.domains.articles.Article
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ArticleRepository extends PagingAndSortingRepository<Article, Long>, CrudRepository<Article, Long> {

    @Query("select a from Article a where a.user.id = :userId and a.isDeleted = :isDeleted")
    Page<Article> findAllByUserIdAndIsDeleted(@Param("userId") Long userId, @Param("isDeleted") Boolean isDeleted, Pageable pageable)

    @Query("select a from Article a where a.user.id = :userId and a.isDeleted = :isDeleted and CAST(a.publicStatus as string) in :publicStatusList")
    Page<Article> findAllByUserIdAndIsDeletedAndPublicStatusIn(@Param("userId") Long userId,
                                                               @Param("isDeleted") Boolean isDeleted,
                                                               @Param("publicStatusList") List<String> publicStatusList,
                                                               Pageable pageable)

    @Query("select a from Article a where (a.user.id = :userId or a.publicStatus = 'PUBLIC' or (a.user.id in :connectedUserIds and a.publicStatus = 'CONNECTED_NETWORK')) and a.isDeleted = :isDeleted order by case when a.user.id in :connectedUserIds then 1 else 2 end, case when a.publicStatus = 'PUBLIC' then 1 else 2 end")
    //return every public article + articles of connected network
    Page<Article> findAllByCurrentUserIdAndConnectedUserIdsOrPublicAndIsDeleted(@Param("userId") Long currentLoggedInUserId,
                                                                                @Param("connectedUserIds") List<Long> connectedUserIds,
                                                                                @Param("isDeleted") Boolean isDeleted,
                                                                                Pageable pageable)


    //If this was production -> the following is wrong -> paging is needed in this situation
    //for the sake of assignment (no time for paging) no paging is implemented
    @Query("select a from Article a where a.user.id in :userIdList")
    List<Article> findAllByUserIds(@Param("userIdList") List<Long> userIdList)
}