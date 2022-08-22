package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*

@Component
class GalleryHandler(
    private val repository: GalleryRepository
) {

    suspend fun getRoot(request: ServerRequest) =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                mapOf("message" to "Hello! There")
            )

    suspend fun getGalleries(request: ServerRequest) =
        ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(repository.findAll())

    suspend fun getGallery(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val gallery = repository.findById(id)

        return ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(gallery)
    }

    suspend fun create(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBody(Gallery::class)
        val galleryId = repository.save(requestBody)

        return ServerResponse
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(repository.findById(galleryId.getValue("id") as Long))
    }
}
