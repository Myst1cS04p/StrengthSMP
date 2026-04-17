plugins {
    java
}

subprojects {
    apply(plugin = "java")

    group = "com.myst1cs04p.strength_smp"
    version = "1.1.1"

    java {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
}
