package com.turbo.gallery.route

import com.turbo.gallery.handler.GalleryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class GalleryRouteConfig(
    @Autowired private val handler: GalleryHandler
) {
    @Bean
    fun routes() = coRouter {
        GET("/", handler::getRoot)
        GET("api/galleries", handler::getGalleries)
        GET("api/galleries/{id}", handler::getGallery)
        POST("api/galleries", handler::createGalleries)
        PUT("api/galleries/{id}", handler::updateGalleries)
        DELETE("api/galleries/{id}", handler::delete)
    }
}
