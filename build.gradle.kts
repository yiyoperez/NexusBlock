import org.apache.tools.ant.filters.ReplaceTokens

val libsPackage = property("libsPackage") as String
val projectVersion = property("version") as String

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://repo.triumphteam.dev/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.6")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")

    implementation("com.github.mqzn:Lotus:1.3")
    implementation("dev.dejvokep:boosted-yaml:1.3.7")
    implementation("net.kyori:adventure-platform-bukkit:4.3.4")
    implementation("dev.triumphteam:triumph-cmd-bukkit:2.0.0-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("NexusBlock-${projectVersion}.jar")

        // Used to locally test the plugin.
        //destinationDirectory.set(file("D:\\MarleyMC Network\\ComboFly\\plugins"))
        destinationDirectory.set(file("$rootDir/bin/"))

        // Relocate libs if any.
        relocate("dev.triumphteam.cmd", "${libsPackage}.commandmanager")
        relocate("dev.dejvokep.boostedyaml", "${libsPackage}.boostedyaml")
        relocate("org.jetbrains.annotations", "${libsPackage}.annotations")
        relocate("org.intellij.lang.annotations", "${libsPackage}.lang-annotations")
    }
    processResources {
        filesMatching("**/*.yml") {
            filter<ReplaceTokens>(
                "tokens" to mapOf("version" to projectVersion)
            )
        }
    }
}