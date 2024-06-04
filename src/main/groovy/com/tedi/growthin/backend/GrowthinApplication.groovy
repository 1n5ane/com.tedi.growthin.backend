package com.tedi.growthin.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties

@SpringBootApplication
@EnableConfigurationProperties
class GrowthinApplication {

    static void main(String[] args) {
        SpringApplication.run(GrowthinApplication, args)
    }

}