package com.turbo.gallery.route

import com.turbo.gallery.handler.GalleryHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class GalleryRouteConfig(
    private val handler: GalleryHandler
) {

    @Bean
    fun routes() = coRouter {
        GET("/", handler::getRoot)
        GET("api/galleries", handler::getGalleries)
        GET("api/galleries/{id}", handler::getGallery)
    }
}
