package com.turbo.image.handler

import com.ninjasquad.springmockk.MockkBean
import com.turbo.image.Image
import com.turbo.image.ImageHandler
import com.turbo.image.ImageRepository
import com.turbo.image.ImageRouteConfig
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
@Import(ImageRouteConfig::class, ImageHandler::class)
internal class ImageHandlerTest {

    @MockkBean
    private lateinit var repository: ImageRepository

    @Autowired
    private lateinit var client: WebTestClient

    private val image = Image.Builder()
        .setName("name")
        .setUrl("url")
        .build()

    private val anotherImage = Image.Builder()
        .setName("another name")
        .setUrl("another url")
        .build()

    @Test
    fun `it should save new image`() {
        val savedImage = slot<Image>()

        coEvery {
            repository.save(capture(savedImage))
        } coAnswers {
            savedImage.captured
        }

        client
            .post()
            .uri("/api/images/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(image)
            .exchange()
            .expectStatus()
            .isCreated
            .expectBody<Image>()
            .isEqualTo(image)
    }

    @Test
    fun `it should return bad request when trying to send request empty body`() {
        val savedImage = slot<Image>()

        coEvery {
            repository.save(capture(savedImage))
        } coAnswers {
            savedImage.captured
        }

        client
            .post()
            .uri("/api/images/")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .body(fromValue("{}"))
            .exchange()
            .expectStatus()
            .isBadRequest
    }

    @Test
    fun `it should return all images`() {
        every {
            repository.findAll()
        } returns flow {
            emit(image)
            emit(anotherImage)
        }

        client.get()
            .uri("/api/images")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList<Image>()
            .hasSize(2)
            .contains(image, anotherImage)
    }

    @Test
    fun `it should return image`() {
        coEvery {
            repository.findById(any())
        } coAnswers {
            image
        }

        client.get()
            .uri("/api/images/1")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Image>()
            .isEqualTo(image)
    }

    @Test
    fun `it should update image`() {
        coEvery {
            repository.findById(any())
        } coAnswers {
            image
        }

        val savedImage = slot<Image>()

        coEvery {
            repository.save(capture(savedImage))
        } coAnswers {
            savedImage.captured
        }

        val updatedImage = Image.Builder()
            .setName("updated name")
            .setUrl("updated url")
            .build()

        client
            .put()
            .uri("/api/images/2")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedImage)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody<Image>()
            .isEqualTo(updatedImage)
    }

    @Test
    fun `it should return not found when trying to update non existing id`() {
        val requestedId = slot<Long>()

        coEvery {
            repository.findById(capture(requestedId))
        } coAnswers {
            nothing
        }

        val updatedImage = Image(name = "New fancy name")

        client
            .put()
            .uri("/api/images/2")
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(updatedImage)
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `delete image with existing id`() {
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
            .uri("/api/images/2")
            .exchange()
            .expectStatus()
            .isNoContent

        coVerify { repository.deleteById(any()) }
    }

    @Test
    fun `Delete image by non-existing id`() {
        coEvery {
            repository.existsById(any())
        } coAnswers {
            false
        }

        client
            .delete()
            .uri("/api/images/2")
            .exchange()
            .expectStatus()
            .isNotFound

        coVerify(exactly = 0) {
            repository.deleteById(any())
        }
    }
}