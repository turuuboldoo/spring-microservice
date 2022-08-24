package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList
import org.springframework.web.reactive.function.BodyInserters.fromValue

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
internal class GalleryHandlerInMemoryTest(
    @Autowired private val repository: GalleryRepository,
    @Autowired private val client: WebTestClient,
) {

    private fun gallery(
        title: String = "title",
        description: String = "description",
    ) = Gallery(
        title = title,
        description = description
    )

    private fun anotherGallery() = gallery(
        title = "another title",
        description = "another description"
    )

    private fun GalleryRepository.seed(vararg galleries: Gallery) =
        runBlocking {
            repository.saveAll(galleries.toList()).toList()
        }

    @AfterEach
    fun afterEach() = runBlocking {
        repository.deleteAll()
    }

    @Test
    fun `it should return all galleries`() {
        repository.seed(gallery(), anotherGallery())

        client.get()
            .uri("/api/gallery")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Gallery>()
            .value<ListBodySpec<Gallery>> { galleries ->
                Assertions.assertThat(galleries[0].title)
                    .isEqualTo(gallery().title)
                Assertions.assertThat(galleries[1].title)
                    .isEqualTo(anotherGallery().title)
            }
            .hasSize(2)
    }

    @Test
    fun `it should return gallery`() {
        repository.seed(gallery(), anotherGallery())

        client.get()
            .uri("/api/gallery/1")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Gallery>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.title)
                    .isEqualTo(gallery().title)

                Assertions.assertThat(response.responseBody?.description)
                    .isEqualTo(gallery().description)
            }
    }

    @Test
    fun `it should return not found when trying to get gallery with non exist id`() {
        client.get()
            .uri("/api/gallery/10")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should store gallery`() {
        client.post()
            .uri("/api/gallery")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(gallery())
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<Gallery>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.title)
                    .isEqualTo(gallery().title)
            }
    }

    @Test
    fun `it should return bad request when create gallery with empty body`() {
        client.post()
            .uri("/api/gallery")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should update gallery`() {
        repository.seed(gallery(), anotherGallery())

        val updatedGallery = gallery(title = "updated title")

        client.put()
            .uri("/api/gallery/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Gallery>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.title)
                    .isEqualTo(updatedGallery.title)
            }
    }

    @Test
    fun `it should return bad request when update gallery with empty request body`() {
        repository.seed(gallery(), anotherGallery())

        client.put()
            .uri("/api/gallery/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should return not found when update gallery with non existing id`() {
        val updatedGallery = gallery(title = "updated title")

        client.put()
            .uri("/api/gallery/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should delete gallery`() {
        repository.seed(gallery(), anotherGallery())

        client.delete()
            .uri("/api/gallery/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `it should return not found when delete gallery with non existing id`() {
        client.delete()
            .uri("/api/gallery/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }
}
