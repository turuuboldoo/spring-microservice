package com.turbo.gallery.repository

import com.turbo.gallery.model.Gallery
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository


interface GalleryRepository : CoroutineCrudRepository<Gallery, Long> {
    fun findAllByOrderByIdDesc(): Flow<Gallery>

}
