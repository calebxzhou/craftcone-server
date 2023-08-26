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
    implementation ("org.jetbrains.exposed:exposed-core:0.42.0")
    implementation ("org.jetbrains.exposed:exposed-dao:0.42.0")
    implementation ("org.jetbrains.exposed:exposed-jdbc:0.42.0")

// https://mvnrepository.com/artifact/org.xerial/sqlite-jdbc
    implementation("org.xerial:sqlite-jdbc:3.42.0.0")
// https://mvnrepository.com/artifact/org.postgresql/postgresql
    implementation("org.postgresql:postgresql:42.6.0")
    // https://mvnrepository.com/artifact/org.mariadb.jdbc/mariadb-java-client
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
// https://mvnrepository.com/artifact/com.oracle.database.jdbc/ojdbc10
    implementation("com.oracle.database.jdbc:ojdbc10:19.20.0.0")
    // https://mvnrepository.com/artifact/com.microsoft.sqlserver/mssql-jdbc
    implementation("com.microsoft.sqlserver:mssql-jdbc:12.4.0.jre11")
// https://mvnrepository.com/artifact/com.h2database/h2
    implementation("com.h2database:h2:2.2.220")
    // https://mvnrepository.com/artifact/com.zaxxer/HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")
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
    mainClass.set("calebxzhou.craftcone.server.AppKt")
}