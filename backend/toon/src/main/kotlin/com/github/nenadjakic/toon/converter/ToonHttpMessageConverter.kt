package com.github.nenadjakic.toon.converter

import com.github.nenadjakic.toon.serializer.ReflectionToonSerializer
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import java.nio.charset.StandardCharsets

val TOON_MEDIA_TYPE = MediaType("application", "toon")

class ToonHttpMessageConverter(
    private val serializer: ReflectionToonSerializer = ReflectionToonSerializer()

) : AbstractHttpMessageConverter<Any>(TOON_MEDIA_TYPE) {

    override fun supports(clazz: Class<*>) = true

    override fun readInternal(
        clazz: Class<*>,
        inputMessage: HttpInputMessage
    ): Any {
        throw UnsupportedOperationException("TOON deserialization not implemented")
    }

    override fun writeInternal(obj: Any, outputMessage: HttpOutputMessage) {
        val content = serializer.serialize(obj)
        outputMessage.body.write(content.toByteArray(StandardCharsets.UTF_8))
    }
}