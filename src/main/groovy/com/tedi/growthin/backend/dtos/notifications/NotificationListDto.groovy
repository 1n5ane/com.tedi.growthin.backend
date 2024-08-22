package com.tedi.growthin.backend.dtos.notifications

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
class NotificationListDto implements Serializable{
    Long totalPages

    List<NotificationDto> notifications

    NotificationListDto() {
        this.notifications = []
    }
}
