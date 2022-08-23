package com.turbo.gallery.route

import com.turbo.gallery.handler.GalleryHandler
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class GalleryRouteConfig(
    @Autowired private val handler: GalleryHandler,
) {

    @Bean
    fun routes() = coRouter {
        GET("/", handler::getRoot)

        "api/gallery".nest {
            accept(MediaType.APPLICATION_JSON)
                .nest {
                    GET("", handler::index)

                    contentType(MediaType.APPLICATION_JSON).nest {
                        POST("", handler::store)
                    }

                    "/{id}".nest {
                        GET("", handler::show)

                        DELETE("", handler::delete)

                        contentType(MediaType.APPLICATION_JSON).nest {
                            PUT("", handler::update)
                        }
                    }
                }
        }
    }
}
