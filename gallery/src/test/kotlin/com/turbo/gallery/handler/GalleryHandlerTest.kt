package com.turbo.gallery.handler

import com.ninjasquad.springmockk.MockkBean
import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import com.turbo.gallery.route.GalleryRouteConfig
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.slot
import kotlinx.coroutines.flow.flow
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters.fromValue

@WebFluxTest
@Import(GalleryRouteConfig::class, GalleryHandler::class)
internal class GalleryHandlerTest {

    @MockkBean
    private lateinit var repository: GalleryRepository

    @Autowired
    private lateinit var client: WebTestClient

    private val gallery = Gallery.Builder()
        .setTitle("title")
        .setDesc("description")
        .build()

    private val anotherGallery = Gallery.Builder()
        .setTitle("another title")
        .setDesc("another description")
        .build()

    @Test
    fun `it should save new gallery`() {
        val savedGallery = slot<Gallery>()

        coEvery {
            repository.save(capture(savedGallery))
        } coAnswers {
            savedGallery.captured
        }

        client
            .post()
            .uri("/api/gallery/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(gallery)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<Gallery>()
            .isEqualTo(gallery)
    }

    @Test
    fun `it should return bad request when trying to send request empty body`() {
        val savedGallery = slot<Gallery>()

        coEvery {
            repository.save(capture(savedGallery))
        } coAnswers {
            savedGallery.captured
        }

        client
            .post()
            .uri("/api/gallery/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should return all galleries`() {
        every {
            repository.findAll()
        } returns flow {
            emit(gallery)
            emit(anotherGallery)
        }

        client.get()
            .uri("/api/gallery")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList<Gallery>()
            .hasSize(2)
            .contains(gallery, anotherGallery)
    }

    @Test
    fun `it should return gallery`() {
        coEvery {
            repository.findById(any())
        } coAnswers {
            gallery
        }

        client.get()
            .uri("/api/gallery/1")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Gallery>()
            .isEqualTo(gallery)
    }

    @Test
    fun `it should update gallery`() {
        coEvery {
            repository.findById(any())
        } coAnswers {
            gallery
        }

        val savedGallery = slot<Gallery>()

        coEvery {
            repository.save(capture(savedGallery))
        } coAnswers {
            savedGallery.captured
        }

        val updatedGallery = Gallery.Builder()
            .setTitle("updated title")
            .setDesc("updated description")
            .build()

        client
            .put()
            .uri("/api/gallery/2")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Gallery>()
            .isEqualTo(updatedGallery)
    }

    @Test
    fun `it should return not found when trying to update non existing id`() {
        val requestedId = slot<Long>()

        coEvery {
            repository.findById(capture(requestedId))
        } coAnswers {
            nothing
        }

        val updatedGallery = Gallery(title = "New fancy title")

        client
            .put()
            .uri("/api/gallery/2")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `delete gallery with existing id`() {
        coEvery {
            repository.existsById(any())
        } coAnswers {
            true
        }

        coEvery {
            repository.deleteById(any())
        } coAnswers {
            nothing
        }

        client
            .delete()
            .uri("/api/gallery/2")
            .exchange()
            .expectStatus()
            .isNoContent

        coVerify { repository.deleteById(any()) }
    }

    @Test
    fun `Delete gallery by non-existing id`() {
        coEvery {
            repository.existsById(any())
        } coAnswers {
            false
        }

        client
            .delete()
            .uri("/api/cats/2")
            .exchange()
            .expectStatus()
            .isNotFound

        coVerify(exactly = 0) {
            repository.deleteById(any())
        }
    }
}