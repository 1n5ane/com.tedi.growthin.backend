package com.tedi.growthin.backend.domains.enums

import jakarta.persistence.Embeddable

@Embeddable
enum AdminRequestStatus {
    PENDING,
    ACCEPTED,
    DECLINED
}