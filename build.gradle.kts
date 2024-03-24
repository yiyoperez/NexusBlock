import org.apache.tools.ant.filters.ReplaceTokens

val libsPackage = property("libsPackage") as String
val projectVersion = property("version") as String

java.sourceCompatibility = JavaVersion.VERSION_1_8

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.6")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("NexusBlock-${projectVersion}.jar")

        //destinationDirectory.set(file("D:\\MarleyMC Network\\ComboFly\\plugins"))
        destinationDirectory.set(file("$rootDir/bin/"))

        // TODO Relocate libs
        //relocate("org.jetbrains.annotations", "${libsPackage}.annotations")
    }
    processResources {
        filesMatching("**/*.yml") {
            filter<ReplaceTokens>(
                "tokens" to mapOf("version" to projectVersion)
            )
        }
    }
}