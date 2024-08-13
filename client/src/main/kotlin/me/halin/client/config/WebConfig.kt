package me.halin.client.config

import co.kr.oliveyoung.User
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
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpInputMessage
import org.springframework.http.HttpOutputMessage
import org.springframework.http.MediaType
import org.springframework.http.converter.AbstractHttpMessageConverter
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.io.ByteArrayOutputStream


@Configuration
class WebConfig : WebMvcConfigurer {
    override fun configureMessageConverters(converters: MutableList<HttpMessageConverter<*>?>) {
        Reflections("co.kr.oliveyoung")
            .get(
                Scanners.TypesAnnotated.with(AvroGenerated::class.java)
            ).filter { !it.toString().endsWith("Builder") }
            .forEach {
//                val avroSchema = Class.forName(it)
//                val instance = (avroSchema.getDeclaredConstructor().newInstance() as SpecificRecordBase)

                converters.add(0, AvroMessageConverter(User.`SCHEMA$`, User::class.java))
            }
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
