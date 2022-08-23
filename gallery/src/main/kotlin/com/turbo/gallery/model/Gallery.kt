package com.turbo.gallery.model

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("galleries")
data class Gallery(
    @Id
    var id: Long? = null,

    @Column
    var title: String? = null,

    @Column
    var description: String? = null,
) {
    class Builder {
        private var id: Long? = null
        private var title: String? = null
        private var description: String? = null

        fun setId(id: Long?): Builder {
            this.id = id
            return this
        }

        fun setTitle(title: String?): Builder {
            this.title = title
            return this
        }

        fun setDesc(description: String?): Builder {
            this.description = description
            return this
        }

        fun build() = Gallery(id, title, description)
    }
}
