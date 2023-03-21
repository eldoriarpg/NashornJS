import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.Permission.Default.OP
import net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("de.chojo.publishdata") version "1.2.4"
    id("net.minecrell.plugin-yml.bukkit") version "0.5.3"
}

repositories {
    maven("https://repo.maven.apache.org/maven2/")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://eldonexus.de/repository/maven-public/")
}

dependencies {
    implementation("de.eldoria", "eldo-util", "1.14.4")
    implementation("org.openjdk.nashorn", "nashorn-core", "15.4")
    compileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
    testCompileOnly("com.destroystokyo.paper", "paper-api", "1.16.5-R0.1-SNAPSHOT")
}

group = "de.eldoria"
version = "1.1.4"
description = "NashornJs"


java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

val shadebade = "de.eldoria.nashornjs.libs."

publishData {
    addBuildData()
    useEldoNexusRepos()
    publishComponent("java")
}

publishing {
    publications.create<MavenPublication>("maven") {
        publishData.configurePublication(this)
    }

    repositories {
        maven {
            authentication {
                credentials(PasswordCredentials::class) {
                    username = System.getenv("NEXUS_USERNAME")
                    password = System.getenv("NEXUS_PASSWORD")
                }
            }
            name = "EldoNexus"
            url = uri(publishData.getRepository())

        }
    }
}

tasks {
    shadowJar {
        relocate("de.eldoria.eldoutilities", shadebade + "eldoutil")
        relocate("org.openjdk.nashorn", shadebade + "js")
        relocate("org.objectweb.asm", shadebade + "asm")
        mergeServiceFiles()
    }

    test {
        useJUnit()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
    compileJava {
        options.encoding = "UTF-8"
    }
}

bukkit {
    name = "NashornJs"
    description = "Nashorn JavaScript Engine for Servers on Java 15 or higher."
    main = "de.eldoria.nashornjs.Nashorn"
    version = publishData.getVersion(true)
    apiVersion = "1.13"
    authors = listOf("OpenJDK", "Hadde")
    load = STARTUP

    commands {
        register("js") {
            aliases = listOf("nashorn", "eval")
            permission = "nashorn.eval"
            description = "Evaluate some JS."
        }
    }

    permissions {
        register("nashorn.eval") {
            default = OP
        }
    }
}


