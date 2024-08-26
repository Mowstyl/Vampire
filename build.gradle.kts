import java.io.ByteArrayOutputStream;


plugins {
    `java-library`
    alias(libs.plugins.shadowPlugin)
    alias(libs.plugins.generatePOMPlugin)
}


val getGitHash: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
}

group = "com.clanjhoo"
version = "1.0.0-SNAPSHOT"//.replace("SNAPSHOT", getGitHash)
description = "Anyone can become a vampire, but do you want to? During daytime vampires cower from sunlight. During the night the humans reach for their holy water and wooden stakes as the vampires roam the lands with inhuman strength, speed and levitation-powers. Driven by their endless bloodlust, they devour all living in their way."

ext.set("projectName", gradle.extra["projectName"].toString())
maven.pom {
    name = gradle.extra["projectName"].toString()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
        vendor = JvmVendorSpec.ORACLE
    }
}

repositories {
    gradlePluginPortal {
        content {
            includeGroup("com.gradleup")
        }
    }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
        content {
            includeGroup("io.papermc.paper")
            includeGroup("com.mojang")
        }
    }
    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
        content {
            includeGroup("co.aikar")
            includeGroup("net.md-5")
        }
    }
    maven {
        url = uri("https://maven.enginehub.org/repo/")
        content {
            includeGroup("com.sk89q.worldedit")
            includeGroup("com.sk89q.worldguard")
        }
    }
    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        content {
            includeGroup("me.clip")
        }
    }
    maven {
        url = uri("https://jitpack.io")
        content {
            includeGroupByRegex("com\\.github\\..*")
        }
    }
    maven {
        url = uri("https://nexus.clanjhoo.com/repository/maven-public/")
        content {
            includeGroup("com.clanjhoo")
            includeGroup("us.rfsmassacre")
        }
    }
    maven {
        url = uri("https://mvn.lumine.io/repository/maven-public/")
        content {
            includeGroup("LibsDisguises")
        }
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
        content {
            includeGroup("com.comphenix.protocol")
        }
    }
    mavenCentral()
    // mavenLocal()
}

dependencies {
    //compileOnly(libs.spigotmc.spigotapi)
    compileOnly(libs.papermc.paperapi)
    compileOnly(libs.sk89q.worldedit.core) {
        isTransitive = false
    }
    compileOnly(libs.sk89q.worldedit.bukkit) {
        isTransitive = false
    }
    compileOnly(libs.sk89q.worldguard.core) {
        isTransitive = false
    }
    compileOnly(libs.sk89q.worldguard.bukkit) {
        isTransitive = false
    }
    compileOnly(libs.libraryaddict.libsdisguises) {
        isTransitive = false
    }
    compileOnly(libs.clip.placeholderapi) {
        isTransitive = false
    }
    compileOnly(libs.milkbowl.vaultapi) {
        isTransitive = false
    }
    //implementation(files("./lib/Werewolf-1.7.2-SNAPSHOT.jar"))
    compileOnly(libs.rfsmassacre.werewolves) {
        isTransitive = false
    }
    compileOnly(libs.comphenix.protocollib) {
        isTransitive = false
    }
    implementation(libs.aikar.acfpaper)
    implementation(libs.clanjhoo.dbhandler)
    implementation(libs.kyori.adventure.minimessage)
    implementation(libs.kyori.adventure.gson)
    implementation(libs.kyori.platform.bukkit)
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand( project.properties )
        }
    }

    shadowJar {
        //archiveFileName.set("${rootProject.name}-${version}.jar")
        relocate("co.aikar.commands", "co.aikar.${rootProject.name.lowercase()}.acf")
        relocate("co.aikar.locales", "co.aikar.${rootProject.name.lowercase()}.locales")
        relocate("com.clanjhoo.dbhandler", "com.clanjhoo.${rootProject.name.lowercase()}.dbhandler")
        relocate("net.kyori", "net.kyori.${rootProject.name.lowercase()}")
        exclude("com/google/gson/**")
        exclude("META-INF/services/**")
        exclude("META-INF/versions/**")
        exclude("META-INF/maven/co.aikar/**")
        exclude("META-INF/maven/com.google.code.gson/**")
        exclude("META-INF/maven/net.jodah/**")
        exclude("META-INF/maven/com.clanjhoo/dbhandler/**")
    }
}
