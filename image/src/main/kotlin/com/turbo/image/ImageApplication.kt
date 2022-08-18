package com.turbo.image

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.server.*

@EnableEurekaClient
@SpringBootApplication
class ImageApplication

fun main(args: Array<String>) {
    runApplication<ImageApplication>(*args)
}

//@RestController
//@RequestMapping
//class HomeController {
//
//    @GetMapping("/")
//    fun hello() = "Hello! There"
//
//    @GetMapping("/api/image")
//    fun getImages() = listOf(
//        Image(1, "Treehouse of Horror V", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3842005760"),
//        Image(2, "The Town", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3698134272"),
//        Image(3, "The Last Traction Hero", "https://www.imdb.com/title/tt0096697/mediaviewer/rm1445594112")
//    )
//
//
//}

@Configuration
@EnableR2dbcRepositories
class DatabaseConfig


@Configuration
class RouteConfig {

    @Bean
    fun myWebClient(webClientBuilder: WebClient.Builder) =
        webClientBuilder
            .baseUrl("http://127.0.0.1")
            .build()
}

@Table("image")
data class Image(
    @Id
    var id: Int,
    @Column
    var name: String,
    @Column
    var url: String,
)


interface ImageRepository : CoroutineCrudRepository<Image, Long>

@Component
class ImageHandler(
    private val repository: ImageRepository
) {

    suspend fun getRoot(request: ServerRequest) =
        ServerResponse.ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValueAndAwait(
                mapOf("message" to "Hello! There")
            )

    suspend fun getImagies(request: ServerRequest) =
        ServerResponse
            .ok()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyAndAwait(repository.findAll())
}

@Configuration
class ImageRouteConfig(
    private val handler: ImageHandler
) {

    @Bean
    fun routes() = coRouter {
        GET("/", handler::getRoot)
        GET("api/image", handler::getRoot)

        "api/image".nest {
            GET("/imagies", handler::getImagies)
        }
    }

}
