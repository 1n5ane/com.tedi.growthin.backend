package com.tedi.growthin.backend.services.utils

import org.springframework.stereotype.Service

import java.time.OffsetDateTime

@Service
class DateTimeService {

    static OffsetDateTime getCurrentOffsetDateTime(){
        return OffsetDateTime.now()
    }
}
