plugins {
    `java-library`
}

repositories {
    mavenLocal()
    maven("https://jitpack.io")
    maven("https://oss.sonatype.org/content/groups/public")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")
    compileOnly("com.github.decentsoftware-eu:decentholograms:2.3.1")
    compileOnly("com.gmail.filoghost.holographicdisplays:holographicdisplays-api:2.4.9")

    implementation("com.github.Zrips:CMILib:4d47533985")
}

group = "xhyrom"
version = "1.1.3"
description = "NexusBlock"
java.sourceCompatibility = JavaVersion.VERSION_1_8


tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
