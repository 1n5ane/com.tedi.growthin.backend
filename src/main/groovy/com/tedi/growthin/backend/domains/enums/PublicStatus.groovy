package com.tedi.growthin.backend.domains.enums

import jakarta.persistence.Embeddable

@Embeddable
enum PublicStatus {
    PUBLIC,
    CONNECTED_NETWORK,
    HIDDEN

    PublicStatus() {}
}