package com.turbo.image

import kotlinx.coroutines.flow.Flow
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.*

@EnableEurekaClient
@SpringBootApplication
class ImageApplication

fun main(args: Array<String>) {
    runApplication<ImageApplication>(*args)
}

@Configuration
@EnableR2dbcRepositories
class DatabaseConfig


@Configuration
class RouteConfig {

    @Bean
    fun myWebClient(webClientBuilder: WebClient.Builder) =
        webClientBuilder
            .baseUrl("http://127.0.0.1")
            .build()
}

@Table("images")
data class Image(
    @Id
    var id: Int,

    @Column
    var name: String,

    @Column
    var url: String,

    @Column("gallery_id")
    var galleryId: Long,
)

interface ImageRepository : CoroutineCrudRepository<Image, Long> {

    fun findByGalleryId(galleryId: Long): Flow<Image>

}

@Component
class ImageHandler(private val repository: ImageRepository) {

    suspend fun getRoot(request: ServerRequest) =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                mapOf("message" to "Hello! There")
            )

    suspend fun getImages(request: ServerRequest): ServerResponse {
        val galleryId = request.queryParam("galleryId")
            .orElse(null)

        if (!galleryId.isNullOrEmpty()) {

            val images = repository.findByGalleryId(galleryId.toLong())

            return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyAndAwait(images)
        }

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(repository.findAll())
    }

    suspend fun getImage(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        val image = repository.findById(id)

        return when {
            image != null -> {
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(image)
            }

            else -> ServerResponse
                .notFound()
                .buildAndAwait()
        }
    }

    suspend fun store(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBody(Image::class)

        if (requestBody.name.isNullOrEmpty() && requestBody.url.isNullOrEmpty()) {
            return ServerResponse
                .badRequest()
                .buildAndAwait()
        }

        val gallery = repository.save(requestBody)

        return ServerResponse
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(gallery)
    }

    suspend fun update(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val requestBody = request.awaitBodyOrNull(Image::class)

        if (requestBody?.name.isNullOrEmpty() && requestBody?.url.isNullOrEmpty()) {
            return ServerResponse
                .badRequest()
                .buildAndAwait()
        }

        val gallery = repository.findById(id)
            ?: return ServerResponse
                .notFound()
                .buildAndAwait()

        val updatedGallery = repository.save(requestBody!!.copy(id = gallery.id))

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(updatedGallery)
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()

        return if (repository.existsById(id)) {
            repository.deleteById(id)

            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(mapOf("message" to "success"))
        } else {
            ServerResponse
                .notFound()
                .buildAndAwait()
        }
    }



}


@Configuration
class ImageRouteConfig(
    private val handler: ImageHandler
) {

    @Bean
    fun routes() = coRouter {
        GET("api/images", handler::getImages)
        GET("api/images/{id}", handler::getImage)
        POST("api/images", handler::store)
        PUT("api/images/{id}", handler::update)
        DELETE("api/images/{id}", handler::delete)
    }
}
