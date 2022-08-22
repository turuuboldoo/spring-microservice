package com.turbo.gallery.repository

import com.turbo.gallery.model.Gallery
import io.r2dbc.spi.Row
import io.r2dbc.spi.RowMetadata
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactor.awaitSingle
import lombok.RequiredArgsConstructor
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.r2dbc.core.awaitOne
import org.springframework.r2dbc.core.awaitSingle
import org.springframework.r2dbc.core.awaitSingleOrNull
import org.springframework.stereotype.Component
import reactor.kotlin.core.publisher.toMono
import java.util.function.BiFunction

@RequiredArgsConstructor
@Component
class GalleryRepository(
    private val client: DatabaseClient
) {

    companion object {
        val MAPPING_FUNCTION: BiFunction<Row, RowMetadata, Gallery> = BiFunction { row, _ ->
            Gallery(
                row.get("id", Long::class.java),
                row.get("title", String::class.java),
                row.get("description", String::class.java)
            )

//            Gallery.Builder()
//                .setId(row.get("id", Long::class.java))
//                .setTitle(row.get("title", String::class.java))
//                .setDesc(row.get("description", String::class.java))
//                .build()
        }
    }

    fun findAll() =
        client.sql("select * from galleries")
            .map(MAPPING_FUNCTION)
            .all()
            .asFlow()

    suspend fun findById(id: Long): Gallery =
        client.sql("select * from galleries where id = :id")
            .bind("id", id)
            .map(MAPPING_FUNCTION)
            .all()
            .awaitFirst()

    suspend fun save(gallery: Gallery) =
        client.sql("insert into galleries (title,description) values (:title,:description)")
            .filter { statement, _ ->
                statement.returnGeneratedValues("id").execute()
            }
            .bind("title", gallery.title ?: "")
            .bind("description", gallery.description ?: "")
            .fetch()
            .first()
            .awaitFirst()
}
