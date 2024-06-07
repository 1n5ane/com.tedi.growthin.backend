package com.tedi.growthin.backend

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties

//TODO: uncomment to autoconfigure db
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class )
@EnableConfigurationProperties
class GrowthinApplication {

    static void main(String[] args) {
        SpringApplication.run(GrowthinApplication, args)
    }

}