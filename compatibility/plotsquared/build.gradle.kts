/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    id("com.ghostchu.java-conventions")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.19.2-R0.1-SNAPSHOT")
    compileOnly("com.plotsquared:PlotSquared-Bukkit:6.10.5")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.13")
    compileOnly(project(":common"))
}

group = "com.ghostchu.quickshop.compatibility"
description = "Compat-PlotSquared"

java {
    withJavadocJar()
}
