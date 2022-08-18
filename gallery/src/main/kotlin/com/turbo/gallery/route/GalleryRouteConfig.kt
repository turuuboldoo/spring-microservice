package com.turbo.gallery.route

import com.turbo.gallery.handler.GalleryHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class GalleryRouteConfig(
    private val handler: GalleryHandler
) {

    @Bean
    fun routes() = coRouter {
        GET("/", handler::getRoot)

        "/api/gallery".nest {
            accept(MediaType.APPLICATION_JSON).nest {
                GET("", handler::getGalleries)
                GET("/{id}", handler::getGalleries)
                POST("", handler::getGalleries)
            }
        }
    }
}
