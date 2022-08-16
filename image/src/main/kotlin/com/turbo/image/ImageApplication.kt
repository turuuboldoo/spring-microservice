package com.turbo.image

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@EnableEurekaClient
@SpringBootApplication
class ImageApplication

fun main(args: Array<String>) {
    runApplication<ImageApplication>(*args)
}

@RestController
@RequestMapping
class HomeController {

    @GetMapping("/")
    fun hello() = "Hello! There"

    @GetMapping("/api/image")
    fun getImages() = listOf(
        Image(1, "Treehouse of Horror V", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3842005760"),
        Image(2, "The Town", "https://www.imdb.com/title/tt0096697/mediaviewer/rm3698134272"),
        Image(3, "The Last Traction Hero", "https://www.imdb.com/title/tt0096697/mediaviewer/rm1445594112")
    )

}

data class Image(
    var id: Int,
    var name: String,
    var url: String,
)
