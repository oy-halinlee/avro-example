plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "avro-sample"
include("server")
include("client")
include("batch")
