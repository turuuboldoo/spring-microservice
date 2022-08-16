package com.turbo.gallery.repository

import com.turbo.gallery.model.Gallery
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface GalleryRepository : CoroutineCrudRepository<Gallery, Long>
