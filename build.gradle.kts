import org.apache.tools.ant.filters.ReplaceTokens

val libsPackage = property("libsPackage") as String
val projectVersion = property("version") as String

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.panda-lang.org/releases")
    maven("https://repo.codemc.io/repository/nms/")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.codemc.io/repository/maven-snapshots/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
}

dependencies {
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.6")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")

    implementation("com.github.mqzn:Lotus:1.3")
    implementation("dev.dejvokep:boosted-yaml:1.3.7")
    implementation("net.wesjd:anvilgui:1.10.3-SNAPSHOT")
    implementation("dev.rollczi:litecommands-bukkit:3.9.2")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }
    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
    shadowJar {
        archiveFileName.set("NexusBlock-${projectVersion}.jar")

        // Used to locally test the plugin.
        //destinationDirectory.set(file("D:\\MarleyMC Network\\ComboFly\\plugins"))
        destinationDirectory.set(file("$rootDir/bin/"))

        // Relocate libs if any.
        relocate("net.wesjd.anvilgui", "${libsPackage}.anvilgui")
        relocate("net.kyori", "${libsPackage}.kyori")
        relocate("dev.dejvokep.boostedyaml", "${libsPackage}.boostedyaml")
        relocate("dev.rollczi.litecommands", "${libsPackage}.litecommands")
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