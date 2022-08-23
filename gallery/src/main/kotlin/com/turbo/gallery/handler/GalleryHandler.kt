package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class GalleryHandler(
    private val repository: GalleryRepository,
) {

    suspend fun getRoot(request: ServerRequest) =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                mapOf("message" to "Hello! There")
            )

    suspend fun index(request: ServerRequest) =
        ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(repository.findAll())

    suspend fun show(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val gallery = repository.findById(id) ?: return ServerResponse.notFound()
            .buildAndAwait()

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(gallery)
    }

    suspend fun store(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBody(Gallery::class)

        if (requestBody.title.isNullOrEmpty() && requestBody.description.isNullOrEmpty()) {
            return ServerResponse
                .badRequest()
                .buildAndAwait()
        }

        val galleryId = repository.save(requestBody)

        return ServerResponse
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(galleryId)
    }

    suspend fun update(request: ServerRequest) =
        ServerResponse
            .badRequest()
            .buildAndAwait()

    suspend fun delete(request: ServerRequest) =
        ServerResponse
            .badRequest()
            .buildAndAwait()
}
