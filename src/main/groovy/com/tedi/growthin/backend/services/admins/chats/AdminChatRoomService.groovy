package com.tedi.growthin.backend.services.admins.chats

import com.tedi.growthin.backend.domains.chat.ChatRoom
import com.tedi.growthin.backend.domains.chat.ChatRoomMessage
import com.tedi.growthin.backend.dtos.chats.ChatRoomMessageDto
import com.tedi.growthin.backend.services.chats.ChatRoomService
import org.springframework.stereotype.Service

@Service
class AdminChatRoomService extends ChatRoomService {

    def exportAllChatRoomDataForUserIds(List<Long> userIds) throws Exception {
        //exportData is a list of the following objects
        //[id:..., relatedUser1:..., relatedUser2:..., createdAt:..., messages:[list of ChatRoomeMessageDtos related to current chatroomDto]]
        def exportData = []

        List<ChatRoom> chatRoomList = this.chatRoomRepository.findAllByUserIds(userIds)
        def chatRoomIds = []
        chatRoomList.each { cr ->
            chatRoomIds.add(cr.id)
        }

        List<ChatRoomMessage> chatRoomMessageList = this.chatRoomMessageRepository.findAllByChatRoomIds(chatRoomIds)


        chatRoomList.each { cr ->
            //filter all messaged from chat room message list for current chatroom id
            List<ChatRoomMessage> messageList = chatRoomMessageList.findAll { crm ->
                crm.chatRoom.id == cr.id
            }

            List<ChatRoomMessageDto> messageDtoList = []
            messageList.each { crm ->
                messageDtoList.add(chatRoomMessageDtoFromChatRoomMessage(crm))
            }

            exportData.add([
                    "id"          : cr.id,
                    "relatedUser1": cr.user1.id,
                    "relatedUser2": cr.user2.id,
                    "createdAt"   : cr.createdAt,
                    "messages"    : messageDtoList
            ])
        }

        return exportData
    }
}
