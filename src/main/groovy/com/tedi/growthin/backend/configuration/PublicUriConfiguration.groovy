package com.tedi.growthin.backend.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod

@Configuration
class PublicUriConfiguration {

    @Bean(name = "PUBLIC_URIS")
    public def skipUrls(){
        //NO JWT FILTER INVOCATION FOR THESE URIS - METHODS
        def SKIP_URLS = [
                [
                        "path": "/api/v1/user",
                        "methods": [HttpMethod.POST.toString()]
                ],
                [
                        "path": "/api/v1/user/exists",
                        "methods": [HttpMethod.GET.toString()]
                ]
        ]

        return SKIP_URLS
    }
}
