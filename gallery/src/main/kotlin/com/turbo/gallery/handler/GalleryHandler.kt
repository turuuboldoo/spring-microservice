package com.turbo.gallery.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.turbo.gallery.extension.asMap
import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import io.github.cdimascio.openapi.Validate
import io.github.cdimascio.openapi.ValidationError
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class GalleryHandler(
    private val repository: GalleryRepository,
) {

    // validation configure required Swagger UI yaml file
    // see `resource/api.yaml` file
    val validate = Validate.configure(
        openApiSwaggerPath = "api.yaml",
        errorHandler = { status, message ->
            ValidationError(status.value(), message[0])
        },
        objectMapperFactory = {
            ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
    )

    suspend fun getRoot(request: ServerRequest) =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                mapOf("message" to "Hello! There")
            )

    suspend fun index(request: ServerRequest) =
        validate.requestAndAwait(request) {
            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyAndAwait(repository.findAllByOrderByIdDesc())
        }

    suspend fun show(request: ServerRequest) =
        validate.requestAndAwait(request) {
            val id = request.pathVariable("id").toLong()

            val gallery = repository.findById(id)
                ?: ServerResponse
                    .notFound()
                    .buildAndAwait()

            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(gallery.asMap())
        }

    suspend fun store(request: ServerRequest) =
        validate.request(request).awaitBody(Gallery::class.java) { body ->
            val gallery = repository.save(body)

            ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(mapOf("data" to gallery))
        }
}
