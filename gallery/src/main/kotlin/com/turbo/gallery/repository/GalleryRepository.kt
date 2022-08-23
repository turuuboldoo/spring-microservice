package com.turbo.gallery.repository

import com.turbo.gallery.model.Gallery
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.r2dbc.core.insert
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Component
import org.springframework.stereotype.Repository
import java.util.function.BiFunction

//@Component
//class GalleryRepository(
//    private val client: DatabaseClient,
//) {
//
//    private var template = R2dbcEntityTemplate(client.connectionFactory)
//
//    companion object {
//        val MAPPING_FUNCTION: BiFunction<Row, RowMetadata, Gallery> = BiFunction { row, _ ->
//            Gallery.Builder()
//                .setId(row.get("id", Long::class.java))
//                .setTitle(row.get("title", String::class.java))
//                .setDesc(row.get("description", String::class.java))
//                .build()
//        }
//    }
//
//    fun findAll() =
//        client.sql("select * from galleries order by id desc")
//            .map(MAPPING_FUNCTION)
//            .all()
//            .asFlow()
//
//    suspend fun findById(id: Long): Gallery? =
//        client.sql("select * from galleries where id = :id")
//            .bind("id", id)
//            .map(MAPPING_FUNCTION)
//            .all()
//            .awaitFirst()
//
//    suspend fun save(gallery: Gallery): Gallery =
//        template.insert<Gallery>()
//            .using(gallery)
//            .awaitFirst()
//}

interface GalleryRepository : CoroutineCrudRepository<Gallery, Long>
