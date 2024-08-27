plugins {
    `java-library`
    alias(libs.plugins.shadowPlugin)
}

repositories {
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
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
        url = uri("https://repo.aikar.co/content/groups/aikar/")
        content {
            includeGroup("co.aikar")
            includeGroup("net.md-5")
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
}

dependencies {
    //compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.18-R0.1-SNAPSHOT")
    compileOnly(libs.aikar.acfpaper)
    compileOnly(libs.clanjhoo.dbhandler)
    compileOnly(libs.kyori.adventure.minimessage)
    compileOnly(libs.kyori.adventure.gson)
    compileOnly(libs.kyori.platform.bukkit)
    compileOnly(libs.libraryaddict.libsdisguises) {
        isTransitive = false
    }
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
    compileOnly(libs.clip.placeholderapi) {
        isTransitive = false
    }
    compileOnly(libs.milkbowl.vaultapi) {
        isTransitive = false
    }
    compileOnly(libs.rfsmassacre.werewolves) {
        isTransitive = false
    }
    compileOnly(libs.comphenix.protocollib) {
        isTransitive = false
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand( project.properties )
        }
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}
