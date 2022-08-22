package com.turbo.gallery.handler

import com.turbo.gallery.model.Gallery
import com.turbo.gallery.repository.GalleryRepository
import com.turbo.gallery.route.GalleryRouteConfig
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.ListBodySpec
import org.springframework.test.web.reactive.server.expectBodyList

@WebFluxTest
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [GalleryHandler::class, GalleryRouteConfig::class])
internal class GalleryHandlerTest {

    @Autowired
    private lateinit var client: WebTestClient

    @MockBean
    private lateinit var repository: GalleryRepository

    @FlowPreview
    @Test
    fun getGalleries() {
        val galleryList = listOf(
            Gallery(1, "gallery_1", "this is gallery_1"),
            Gallery(2, "gallery_2", "this is gallery_2"),
            Gallery(3, "gallery_3", "this is gallery_3")
        )

        BDDMockito.given(repository.findAll()).willReturn(galleryList.asFlow())

        client.get()
            .uri("/api/gallery")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList<Gallery>()
            .value<ListBodySpec<Gallery>> {
                println("123123 get Galleries $it")
                Assertions.assertEquals(it, galleryList)
            }
    }

    @Test
    fun getGallery() {
        val gallery = Gallery(2, "gallery_2", "this is gallery_2")

        runBlocking {
            BDDMockito.given(repository.findById(gallery.id ?: 0)).willReturn(gallery)
        }

        client.get()
            .uri("/api/gallery/${gallery.id}")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Gallery::class.java)
            .value {
                println("123123 get Gallery $it")
                Assertions.assertEquals(it, gallery)
            }
    }

//    @Test
//    fun createGallery() {
//        val gallery = Gallery(2, "gallery_2", "this is gallery_2")
//
////        runBlocking {
////            BDDMockito.given(repository.save(gallery)).willReturn(
////                mapOf("id", gallery.id)
////            )
////        }
//
//        client.get()
//            .uri("/api/gallery/${gallery.id}")
//            .accept(MediaType.APPLICATION_JSON)
//            .exchange()
//            .expectStatus()
//            .isOk
//            .expectBody(Gallery::class.java)
//            .value {
//                println("123123 get Gallery $it")
//                Assertions.assertEquals(it, gallery)
//            }
//    }
}
