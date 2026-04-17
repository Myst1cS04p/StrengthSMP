import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `java-library`
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":common"))

    // bStats — shaded + relocated
    implementation("org.bstats:bstats-bukkit:3.1.0")

    // Adventure — all shaded and relocated.
    // Spigot 1.21.8 bundles an older Adventure build that is missing
    // Sound$Emitter and other classes introduced in 4.17.0, so we cannot
    // rely on the server-provided copy. Shade the full stack instead.
    implementation("net.kyori:adventure-api:4.17.0")
    implementation("net.kyori:adventure-key:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
    implementation("net.kyori:adventure-text-serializer-gson:4.17.0")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT")
}

tasks.processResources {
    filesMatching("*.yml") {
        expand("version" to project.version)
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveBaseName.set("strengthsmp-bukkit")

    // Relocate everything we shade so we don't conflict with other plugins
    // that may also be shading Adventure.
    relocate("net.kyori.adventure", "com.myst1cs04p.strength_smp.libs.adventure")
    relocate("net.kyori.examination", "com.myst1cs04p.strength_smp.libs.examination")
    relocate("org.bstats", "com.myst1cs04p.strength_smp.libs.bstats")
    relocate("com.google.gson", "com.myst1cs04p.strength_smp.libs.gson")

    dependencies {
        exclude(dependency("org.spigotmc:spigot-api"))
    }

    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.withType<ShadowJar>())
}