package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.model.GalleryDto
import com.turbo.gallery.model.Image
import com.turbo.gallery.repository.GalleryRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.server.*
import java.net.URI

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

    @Autowired
    private lateinit var client: WebClient

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

            println(body)

            galleryDto.id = gallery?.id
            galleryDto.title = gallery?.title
            galleryDto.description = gallery?.description
            galleryDto.image = body

            return when {
                galleryDto != null -> {
                    ServerResponse
                        .ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValueAndAwait(galleryDto)
                }

                else -> ServerResponse
                    .notFound()
                    .buildAndAwait()
            }
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
        val data = request.awaitBody(Gallery::class)
        val gallery = repository.save(data)

        return ServerResponse
            .created(URI.create("api/createGalleries"))
            .contentType(MediaType.APPLICATION_JSON)
            .buildAndAwait()
    }
}
