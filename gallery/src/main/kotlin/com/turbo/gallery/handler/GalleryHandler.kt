package com.turbo.gallery.handler

import com.turbo.gallery.model.Image
import com.turbo.gallery.repository.GalleryRepository
import org.springframework.beans.factory.annotation.Autowired
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

    @Autowired
    private lateinit var client: WebClient

    suspend fun getGallery(request: ServerRequest): ServerResponse {
        val id = request.pathVariable("id").toLong()
        val gallery = repository.findById(id)

        val body = client
            .get()
            .uri("http://localhost:8080/api/image/${id}")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .awaitBody<List<Image>>()

        println(body)

        gallery?.image = body

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
}

