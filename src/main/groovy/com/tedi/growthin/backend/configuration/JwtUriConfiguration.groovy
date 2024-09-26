package com.tedi.growthin.backend.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class JwtUriConfiguration {
    @Value('${application.security.oauth2.resourceserver.jwt.issuer-uri}')
    String issuerUri

    @Value('${application.security.oauth2.resourceserver.jwt.jwk-set-uri}')
    String jwkSetUri
}
