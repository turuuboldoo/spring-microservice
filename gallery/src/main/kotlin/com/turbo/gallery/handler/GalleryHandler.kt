package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.model.GalleryDto
import com.turbo.gallery.model.Image
import com.turbo.gallery.repository.GalleryRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
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

    private var client: WebClient = WebClient.builder()
        .baseUrl("http://127.0.0.1")
        .build()

    suspend fun getGallery(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val gallery = repository.findById(id)
        val galleryDto = GalleryDto()

        val with = request.queryParam("with")
            .orElse(null)

        if (!with.isNullOrEmpty() && with.equals("images")) {
            val body = client.get()
                .uri("http://localhost:8085/api/images?galleryId=${id}")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody<List<Image>>()

            galleryDto.id = gallery?.id
            galleryDto.title = gallery?.title
            galleryDto.description = gallery?.description
            galleryDto.image = body

            return ServerResponse
                .ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValueAndAwait(galleryDto)
        }

        return when {
            gallery != null -> {
                ServerResponse
                    .ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValueAndAwait(gallery)
            }

            else -> ServerResponse
                .notFound()
                .buildAndAwait()
        }
    }

    suspend fun createGalleries(request: ServerRequest): ServerResponse {
        val requestBody = request.awaitBody(Gallery::class)

        if (requestBody.title.isNullOrEmpty() && requestBody.description.isNullOrEmpty()) {
            return ServerResponse
                .badRequest()
                .buildAndAwait()
        }

        val gallery = repository.save(requestBody)

//        return ServerResponse
//            .created(URI.create("api/galleries"))
//            .contentType(MediaType.APPLICATION_JSON).bodyValueAndAwait(gallery)
        return ServerResponse
            .status(201)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(gallery)
    }

    suspend fun updateGalleries(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val requestBody = request.awaitBodyOrNull(Gallery::class)

        if (requestBody?.title.isNullOrEmpty() || requestBody?.description.isNullOrEmpty()) {
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

