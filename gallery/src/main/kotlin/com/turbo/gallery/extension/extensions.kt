package com.turbo.gallery.extension

import kotlin.reflect.full.memberProperties

inline fun <reified T : Any> T.asMap(): Map<String, Any?> {
    val props = T::class.memberProperties.associateBy { it.name }
    return mapOf("data" to props.keys.associateWith { props[it]?.get(this) })
}
