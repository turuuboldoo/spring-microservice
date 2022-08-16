package com.turbo.gallery.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class RouteConfig {

    @Bean
    fun myWebClient(webClientBuilder: WebClient.Builder) =
        webClientBuilder
            .baseUrl("http://127.0.0.1")
            .build()
}
