package me.halin.me.halin.batch.config

import org.apache.avro.Schema
import org.apache.avro.io.BinaryEncoder
import org.apache.avro.io.DatumWriter
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.apache.avro.specific.AvroGenerated
import org.apache.avro.specific.SpecificDatumReader
import org.apache.avro.specific.SpecificDatumWriter
import org.apache.avro.specific.SpecificRecordBase
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.web.client.RestTemplate
import java.io.ByteArrayOutputStream

@Configuration
class WebConfig {
    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate {
        val restTemplate = builder.build()

        Reflections("co.kr.oliveyoung")
            .get(
                Scanners.SubTypes.with(SpecificRecordBase::class.java)
            ).forEach {
                val avroSchema = Class.forName(it)
                val instance = (avroSchema.getDeclaredConstructor().newInstance() as SpecificRecordBase)

                restTemplate.messageConverters.add(0, AvroMessageConverter(instance.schema, instance::class.java))
            }

        return restTemplate
    }
}

class AvroMessageConverter<T : SpecificRecordBase>(
    private val schema: Schema,
    private val targetType: Class<T>
) : AbstractHttpMessageConverter<T>(MediaType("application", "avro")) {

    override fun supports(clazz: Class<*>): Boolean {
        return targetType.isAssignableFrom(clazz)
    }

    override fun readInternal(clazz: Class<out T>, inputMessage: HttpInputMessage): T {
        val datumReader = SpecificDatumReader<T>(schema)
        val decoder = DecoderFactory.get().binaryDecoder(inputMessage.body, null)
        return datumReader.read(null, decoder)
    }

    override fun writeInternal(t: T, outputMessage: HttpOutputMessage) {
        val datumWriter: DatumWriter<T> = SpecificDatumWriter(schema)
        val byteArrayOutputStream = ByteArrayOutputStream()
        val encoder: BinaryEncoder = EncoderFactory.get().binaryEncoder(byteArrayOutputStream, null)
        datumWriter.write(t, encoder)
        encoder.flush()
        byteArrayOutputStream.writeTo(outputMessage.body)
    }
}
