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
)
