import org.jetbrains.kotlin.de.undercouch.gradle.tasks.download.Download


dependencies{
    implementation("org.opensearch.plugin:opensearch-sql-plugin:2.16.0.0")
}

val avroResourceDir = "$projectDir/src/main/avro"


// makeAvroDir task
tasks.register("makeAvroDir") {
    doLast {
        val dir = File(avroResourceDir)
        if (!dir.exists()) {
            mkdir(dir)
        }
    }
}

// downloadAvro task
tasks.register<Download>("downloadAvro") {
    dependsOn("makeAvroDir")
    src("https://raw.githubusercontent.com/oy-halinlee/avro-schema/main/User.avsc")
    dest(file(avroResourceDir))
}

// buildWithAvro task
tasks.register("buildWithAvro") {
    dependsOn("clean", "downloadAvro", "build")

    tasks.named("downloadAvro").get().mustRunAfter("clean")
    tasks.named("build").get().mustRunAfter("downloadAvro")
}


kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}
