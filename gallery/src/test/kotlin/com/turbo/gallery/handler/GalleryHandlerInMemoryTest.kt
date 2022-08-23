package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
internal class GalleryHandlerInMemoryTest {

    @Autowired
    private lateinit var repository: GalleryRepository

    @Autowired
    private lateinit var client: WebTestClient

    private fun aGallery(
        title: String = "title",
        description: String = "description",
    ) = Gallery(
        title = title,
        description = description
    )

    private fun anotherGallery(
        title: String = "another title",
        description: String = "another description",
    ) = aGallery(
        title = title,
        description = description
    )

    private fun GalleryRepository.seed(vararg galleries: Gallery) =
        runBlocking {
            repository.saveAll(galleries.toList()).toList()
        }

    @AfterEach
    fun afterEach() {
        runBlocking {
            repository.deleteAll()
        }
    }

    @Test
    fun `it should return all galleries`() {
        repository.seed(aGallery(), anotherGallery())

        client.get()
            .uri("/api/gallery")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList<Gallery>()
            .hasSize(2)
//            .contains(aGallery(), anotherGallery())
    }
}
