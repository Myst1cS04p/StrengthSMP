import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    // Paper stacks on bukkit - gets common transitively
    implementation(project(":bukkit"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    // Adventure is native to Paper - compileOnly, never shade
    compileOnly("net.kyori:adventure-api:4.17.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveBaseName.set("strengthsmp-paper")

    // Keep relocations consistent with bukkit module
    relocate("org.bstats", "com.myst1cs04p.strength_smp.libs.bstats")
    relocate("com.google.gson", "com.myst1cs04p.strength_smp.libs.gson")

    dependencies {
        exclude(dependency("io.papermc.paper:paper-api"))
        exclude(dependency("net.kyori:adventure-api"))
    }

    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}