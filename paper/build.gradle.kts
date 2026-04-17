import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    id("com.gradleup.shadow") version "8.3.0"
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(project(":bukkit"))

    compileOnly("io.papermc.paper:paper-api:1.21.1-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-api:4.17.0")
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    archiveBaseName.set("strengthsmp-paper")

    relocate("org.bstats", "com.myst1cs04p.strength_smp.libs.bstats")
    relocate("com.google.gson", "com.myst1cs04p.strength_smp.libs.gson")

    dependencies {
        exclude(dependency("io.papermc.paper:paper-api"))
        exclude(dependency("net.kyori:adventure-api"))
    }

    mergeServiceFiles()
}

tasks.processResources {
    filesMatching("*.yml") {
        expand("version" to project.version)
    }
}

tasks.build {
    dependsOn(tasks.withType<ShadowJar>())
}