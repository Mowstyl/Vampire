import java.io.ByteArrayOutputStream;


plugins {
    java
    `maven-publish`
    alias(libs.plugins.shadowPlugin)
}

group = "com.clanjhoo"
version = "1.0.0-SNAPSHOT"
description = "Anyone can become a vampire, but do you want to? During daytime vampires cower from sunlight. During the night the humans reach for their holy water and wooden stakes as the vampires roam the lands with inhuman strength, speed and levitation-powers. Driven by their endless bloodlust, they devour all living in their way."

val getGitHash: String by lazy {
    val stdout = ByteArrayOutputStream()
    rootProject.exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = stdout
    }
    stdout.toString().trim()
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
    maven("https://papermc.io/repo/repository/maven-public/") {
        content {
            includeGroup("io.papermc.paper")
        }
    }
    maven("https://repo.aikar.co/content/groups/aikar/") {
        content {
            includeGroup("co.aikar")
            includeGroup("net.md-5")
        }
    }
    maven("https://maven.enginehub.org/repo/") {
        content {
            includeGroup("com.sk89q.worldedit")
            includeGroup("com.sk89q.worldguard")
        }
    }
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content {
            includeGroup("me.clip")
        }
    }
    maven("https://jitpack.io") {
        content {
            includeGroupByRegex("com\\.github\\..*")
        }
    }
    maven("https://nexus.clanjhoo.com/repository/maven-public/") {
        content {
            includeGroup("com.clanjhoo")
        }
    }
    maven("https://mvn.lumine.io/repository/maven-public/") {
        content {
            includeGroup("LibsDisguises")
        }
    }
    mavenCentral()
    mavenLocal()
}

dependencies {
    compileOnly(libs.papermc.paperapi)
    implementation(libs.aikar.acfpaper) {
        isTransitive = false
    }
    implementation(libs.clanjhoo.dbhandler) {
        isTransitive = false
    }
    implementation(libs.sk89q.worldedit.core) {
        isTransitive = false
    }
    implementation(libs.sk89q.worldedit.bukkit) {
        isTransitive = false
    }
    implementation(libs.sk89q.worldguard.core) {
        isTransitive = false
    }
    implementation(libs.sk89q.worldguard.bukkit) {
        isTransitive = false
    }
    implementation(libs.libraryaddict.libsdisguises) {
        isTransitive = false
    }
    implementation(libs.clip.placeholderapi) {
        isTransitive = false
    }
    implementation(libs.milkbowl.vaultapi) {
        isTransitive = false
    }
    implementation(files("./lib/Werewolf-1.7.1-SNAPSHOT.jar"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand( project.properties )
        }
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}Revamp-${version}.jar".replace("SNAPSHOT", getGitHash))
        relocate("co.aikar.commands", "co.aikar.${rootProject.name.lowercase()}.acf")
        relocate("co.aikar.locales", "co.aikar.${rootProject.name.lowercase()}.locales")
        relocate("com.clanjhoo.dbhandler", "com.clanjhoo.${rootProject.name.lowercase()}.dbhandler")
        include("acf-paper-*-SNAPSHOT.jar")
        include("DBHandler-*.jar")
        include("acf-core_*.properties")
        include("co/aikar/**")
        include("co/aikar/**")
        include("com/clanjhoo/**")
        include("com/zaxxer/hikari/**")
        include("*.yml")
        include("locales/*.yml")
    }
}
