package com.turbo.gallery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.EnableEurekaClient

@EnableEurekaClient
@SpringBootApplication
class GalleryApplication

fun main(args: Array<String>) {
    runApplication<GalleryApplication>(*args)
}
