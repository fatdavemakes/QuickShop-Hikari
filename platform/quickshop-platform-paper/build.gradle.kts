/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.ghostchu.java-conventions")
}

dependencies {
    compileOnly(project(":quickshop-platform-interface"))
    compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
}

description = "quickshop-platform-paper"

java {
    withJavadocJar()
}
