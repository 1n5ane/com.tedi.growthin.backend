package com.tedi.growthin.backend.repositories.chats

import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomMessageRepository extends PagingAndSortingRepository<ChatRoomMessage, Long>, CrudRepository<ChatRoomMessage, Long> {

}
