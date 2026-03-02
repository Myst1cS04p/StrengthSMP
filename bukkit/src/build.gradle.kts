import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":common"))

    implementation("org.bstats:bstats-bukkit:3.1.0")

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:4.17.0")

    compileOnly("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks.processResources {
    filesMatching("*.yml") {
        expand("version" to project.version)
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveBaseName.set("strengthsmp-bukkit")

    relocate("org.bstats", "com.myst1cs04p.strength_smp.libs.bstats")

    relocate("com.google.gson", "com.myst1cs04p.strength_smp.libs.gson")

    dependencies {
        exclude(dependency("org.spigotmc:spigot-api"))
        exclude(dependency("net.kyori:adventure-api"))
    }

    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}