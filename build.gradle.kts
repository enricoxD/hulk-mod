val javaVersion = 17
val silkVersion = "1.10.0"

plugins {
    kotlin("jvm") version "1.8.22"
    id("fabric-loom") version "1.1-SNAPSHOT"
    kotlin("plugin.serialization") version "1.8.22"
}

group = "gg.norisk"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://maven.kosmx.dev/")
    maven("https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/")

    maven {
        url = uri("https://maven.norisk.gg/repository/maven-snapshots/")

        credentials {
            username = (System.getenv("NORISK_NEXUS_USERNAME") ?: project.findProperty("noriskMavenUsername") ?: "").toString()
            password = (System.getenv("NORISK_NEXUS_PASSWORD") ?: project.findProperty("noriskMavenPassword") ?: "").toString()
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:1.20.1")
    mappings("net.fabricmc:yarn:1.20.1+build.2")

    include(modImplementation("gg.norisk:hero-api:1.0.39-SNAPSHOT")!!)
    modImplementation("de.hglabor:notify:1.2.2")

    modImplementation("net.fabricmc:fabric-loader:0.14.21")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.83.1+1.20.1")
    modImplementation("net.fabricmc:fabric-language-kotlin:1.9.5+kotlin.1.8.22")
    modImplementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    modImplementation("net.silkmc:silk-core:$silkVersion")
    modImplementation("net.silkmc:silk-network:$silkVersion")
    modImplementation("net.silkmc:silk-commands:$silkVersion")
    modImplementation("software.bernie.geckolib:geckolib-fabric-1.20.1:4.2.4")
    include(modImplementation("dev.kosmx.player-anim:player-animation-lib-fabric:1.0.2-rc1+1.20")!!)
}

loom {
    accessWidenerPath.set(file("src/main/resources/hulk.accesswidener"))
}

tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjdk-release=$javaVersion", "-Xskip-prerelease-check")
            jvmTarget = "$javaVersion"
        }
    }
    compileJava {
        options.encoding = "UTF-8"
        options.release.set(javaVersion)
    }
    processResources {
        val properties = mapOf("version" to project.version)
        inputs.properties(properties)
        filesMatching("fabric.mod.json") { expand(properties) }
    }
}
