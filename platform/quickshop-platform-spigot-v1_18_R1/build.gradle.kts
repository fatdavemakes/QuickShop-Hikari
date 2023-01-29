/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.ghostchu.java-conventions")
}

dependencies {
    compileOnly(project(":quickshop-platform-spigot-abstract"))
    compileOnly("org.spigotmc:spigot:1.18.1-R0.1-SNAPSHOT")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.11.1")
}

description = "quickshop-platform-spigot-v1_18_R1"

java {
    withJavadocJar()
}
