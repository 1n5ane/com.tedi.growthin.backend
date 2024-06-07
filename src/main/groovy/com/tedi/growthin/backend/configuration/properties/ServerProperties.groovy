package com.tedi.growthin.backend.configuration.properties

import com.tedi.growthin.backend.configuration.SslConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class ServerProperties {

    @Autowired
    SslConfiguration sslConfiguration

    @Value('${server.port}')
    int port

    @Value('${server.address}')
    String address

    String getServerUrl() {
        def scheme = "http"
        if(!sslConfiguration.enabled)
            scheme = "https"
        return scheme + "://" + address + ":" + port
    }
}
