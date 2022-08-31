package com.turbo.image.handler


import com.turbo.image.Image
import com.turbo.image.ImageRepository
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
    @Autowired private val repository: ImageRepository,
    @Autowired private val client: WebTestClient,
) {

    private fun image(
        name: String = "name",
        url: String = "url",
    ) = Image(
        name = name,
        url = url
    )

    private fun anotherImage() = image(
        name = "another name",
        url = "another url"
    )

    private fun ImageRepository.seed(vararg  images: Image) =
        runBlocking {
            repository.saveAll(images.toList()).toList()
        }

    @AfterEach
    fun afterEach() = runBlocking {
        repository.deleteAll()
    }

    @Test
    fun `it should return all galleries`() {
        repository.seed(image(), anotherImage())

        client.get()
            .uri("/api/images")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Image>()
            .value<ListBodySpec<Image>> { galleries ->
                Assertions.assertThat(galleries[0].name)
                    .isEqualTo(Image().name)
                Assertions.assertThat(galleries[1].name)
                    .isEqualTo(anotherImage().name)
            }
            .hasSize(2)
    }

    @Test
    fun `it should return gallery`() {
        repository.seed(image(), anotherImage())

        client.get()
            .uri("/api/images/1")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Image>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.name)
                    .isEqualTo(image().name)

                Assertions.assertThat(response.responseBody?.url)
                    .isEqualTo(image().url)
            }
    }

    @Test
    fun `it should return not found when trying to get gallery with non exist id`() {
        client.get()
            .uri("/api/images/10")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should store gallery`() {
        client.post()
            .uri("/api/images")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(image())
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<Image>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.name)
                    .isEqualTo(image().name)
            }
    }

    @Test
    fun `it should return bad request when create gallery with empty body`() {
        client.post()
            .uri("/api/images")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should update gallery`() {
        repository.seed(image(), anotherImage())

        val updatedGallery = image(name = "updated name")

        client.put()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Image>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.name)
                    .isEqualTo(updatedGallery.name)
            }
    }

    @Test
    fun `it should return bad request when update gallery with empty request body`() {
        repository.seed(image(), anotherImage())

        client.put()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should return not found when update gallery with non existing id`() {
        val updatedGallery = image(name = "updated name")

        client.put()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedGallery)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should delete gallery`() {
        repository.seed(image(), anotherImage())

        client.delete()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `it should return not found when delete gallery with non existing id`() {
        client.delete()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }
}