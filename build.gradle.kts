plugins {
    kotlin("jvm") version "1.9.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    kotlin("plugin.serialization") version "1.9.0"
    application
}

group = "calebxzhou.craftcone.server"
version = "0.0.3"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    // https://mvnrepository.com/artifact/io.netty/netty-all
    implementation("io.netty:netty-all:4.1.94.Final")
    // https://mvnrepository.com/artifact/io.github.microutils/kotlin-logging
    implementation("io.github.microutils:kotlin-logging:3.0.5")
// https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
    implementation("ch.qos.logback:logback-classic:1.4.8")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.mongodb:mongodb-driver-kotlin-coroutine:4.10.2")
// https://mvnrepository.com/artifact/com.akuleshov7/ktoml-core
    implementation("com.akuleshov7:ktoml-core:0.5.0")
    implementation(kotlin("stdlib-jdk8"))
}
tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass.set("calebxzhou.craftcone.server.ConeServerKt")
}