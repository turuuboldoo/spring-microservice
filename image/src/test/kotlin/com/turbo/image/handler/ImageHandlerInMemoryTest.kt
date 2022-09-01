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
internal class ImageHandlerInMemoryTest {

    @Autowired
    private lateinit var repository: ImageRepository

    @Autowired
    private lateinit var client: WebTestClient

    private fun image(
        name: String = "name",
        url: String = "url",
        galleryId: Long = 1,
    ) = Image(
        name = name,
        url = url,
        galleryId = galleryId
    )

    private fun anotherImage() = image(
        name = "another name",
        url = "another url",
        galleryId = 1
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
    fun `it should return all images`() {
        repository.seed(image(), anotherImage())

        client.get()
            .uri("/api/images")
            .exchange()
            .expectStatus()
            .isOk
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBodyList<Image>()
            .value<ListBodySpec<Image>> { images ->
                Assertions.assertThat(images[0].name)
                    .isEqualTo(image().name)
                Assertions.assertThat(images[1].name)
                    .isEqualTo(anotherImage().name)
            }
            .hasSize(2)
    }

    @Test
    fun `it should return image`() {
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
    fun `it should return not found when trying to get image with non exist id`() {
        client.get()
            .uri("/api/images/10")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should store image`() {
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
    fun `it should return bad request when create image with empty body`() {
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
    fun `it should update image`() {
        repository.seed(image(), anotherImage())

        val updatedImage = image(name = "updated name")

        client.put()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedImage)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Image>()
            .consumeWith { response ->
                Assertions.assertThat(response.responseBody?.name)
                    .isEqualTo(updatedImage.name)
            }
    }

    @Test
    fun `it should return bad request when update image with empty request body`() {
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
    fun `it should return not found when update image with non existing id`() {
        val updatedImage = image(name = "updated name")

        client.put()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedImage)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `it should delete image`() {
        repository.seed(image(), anotherImage())

        client.delete()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
    }

    @Test
    fun `it should return not found when delete image with non existing id`() {
        client.delete()
            .uri("/api/images/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound
    }
}