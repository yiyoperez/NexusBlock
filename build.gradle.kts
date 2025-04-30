import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

val libsPackage = property("libsPackage") as String
val projectVersion = property("version") as String
val projectGroup = property("group") as String

plugins {
    `java-library`
    id("com.gradleup.shadow") version "8.3.5"
    id("io.github.revxrsal.zapper") version "1.0.3"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
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
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.8.12")
    compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.5")

    implementation("net.wesjd:anvilgui:1.10.3-SNAPSHOT")

    zap("dev.dejvokep:boosted-yaml:1.3.7")
    zap("dev.triumphteam:triumph-gui:3.1.11")
    zap("dev.rollczi:litecommands-bukkit:3.9.6")
    zap("net.kyori:adventure-platform-bukkit:4.3.4")
    zap("net.kyori:adventure-text-minimessage:4.21.0")
    zap("net.kyori:adventure-text-serializer-legacy:4.17.0")
}

tasks.withType(xyz.jpenilla.runtask.task.AbstractRun::class) {
    javaLauncher = javaToolchains.launcherFor {
        vendor = JvmVendorSpec.JETBRAINS
        languageVersion = JavaLanguageVersion.of(21)
    }
    jvmArgs("-XX:+AllowEnhancedClassRedefinition")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        options.compilerArgs.add("-parameters")
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    runServer {
        minecraftVersion("1.20.4")

        downloadPlugins {
            modrinth("decentholograms", "2.8.12")
            hangar("placeholderapi", "2.11.6")
            url("https://ci.viaversion.com/job/ViaVersion/1077/artifact/build/libs/ViaVersion-5.2.2-SNAPSHOT.jar")
            url("https://download.luckperms.net/1567/bukkit/loader/LuckPerms-Bukkit-5.4.150.jar")
            url("https://github.com/EternalCodeTeam/EternalCore/releases/download/v1.5.2/EternalCore.v1.5.2.MC.1.17.x-1.21.x.jar")
        }
    }

    shadowJar {
        archiveFileName.set("NexusBlock-${projectVersion}.jar")

        destinationDirectory.set(file("$rootDir/bin/"))
    }

    zapper {
        libsFolder = "libraries"
        relocationPrefix = libsPackage

        repositories { includeProjectRepositories() }

        // Relocation needs to be commented out when debugging with runServer!!
//        relocate("net.kyori", "kyori")
//        relocate("net.wesjd.anvilgui", "anvilgui")
//        relocate("io.github.mqzen.menus", "lotus")
//        relocate("dev.dejvokep.boostedyaml", "boostedyaml")
//        relocate("dev.rollczi.litecommands", "litecommands")
        //relocate("org.jetbrains.annotations", "annotations")
        //relocate("org.intellij.lang.annotations", "lang-annotations")
    }

    bukkit {
        name = "NexusBlock"
        prefix = name
        version = projectVersion
        main = "$projectGroup.$name"
        apiVersion = "1.20"
        authors = listOf("Sliide_")
        contributors = listOf("xHyroM")
        description = "Plugin that allows create nexus blocks."
        softDepend = listOf(
            "HolographicDisplays",
            "DecentHolograms",
            "Multiverse-Core",
            "MultiWorld",
            "My_Worlds"
        )

        permissions {
            register("nexusblock.admin.break") {
                default = BukkitPluginDescription.Permission.Default.OP
                description = "Allow the player to break placed nexus blocks when sneaking."
            }
        }
    }
}