
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "mobi.sevenwinds"
version = "0.0.1-SNAPSHOT"

application {
    mainClass.set("mobi.sevenwinds.ApplicationKt")
    mainClassName = "mobi.sevenwinds.ApplicationKt"
}

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-jackson:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")
    implementation("io.ktor:ktor-locations:$ktor_version")
    implementation("io.ktor:ktor-metrics:$ktor_version")
    implementation("io.ktor:ktor-server-sessions:$ktor_version")

    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("com.github.papsign:Ktor-OpenAPI-Generator:0.2-beta.20")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.9.8") // needed for multipart parsing
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.9.8") // needed for Optional<> parsing
    implementation("org.webjars:swagger-ui:3.25.0")
    implementation("org.reflections:reflections:0.9.11") // only used while initializing

    implementation("at.favre.lib:bcrypt:0.9.0")

    implementation("org.postgresql:postgresql:42.2.12")

    implementation("org.jetbrains.exposed:exposed:0.17.13")
    implementation("com.zaxxer:HikariCP:2.7.8")
    implementation("org.flywaydb:flyway-core:5.2.4")

    implementation("com.squareup.retrofit2:retrofit:2.3.0")
    implementation("com.squareup.retrofit2:converter-jackson:2.3.0")
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.3.0")
    implementation("com.squareup.okhttp3:logging-interceptor:3.10.0")

    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    testImplementation("org.assertj:assertj-core:3.19.0")
    testImplementation("io.rest-assured:rest-assured:4.3.3")
    testImplementation("org.hamcrest:hamcrest:2.2")
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs += "-Xuse-experimental=kotlin.ExperimentalStdlibApi"
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}