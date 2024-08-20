import java.io.ByteArrayOutputStream;


plugins {
    java
    `maven-publish`
    alias(libs.plugins.shadowPlugin)
    // id("io.papermc.paperweight.userdev") version "1.7.2"
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
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
        content {
            includeGroup("io.papermc.paper")
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
    //mavenLocal()
}

dependencies {
    // paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    compileOnly(libs.papermc.paperapi)
    implementation(libs.aikar.acfpaper) {
        isTransitive = false
    }
    implementation(libs.clanjhoo.dbhandler) {
        isTransitive = false
    }
    implementation(libs.zaxxer.hikaricp) {
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
    //implementation(files("./lib/Werewolf-1.7.2-SNAPSHOT.jar"))
    implementation(libs.rfsmassacre.werewolves) {
        isTransitive = false
    }
    implementation(libs.comphenix.protocollib) {
        isTransitive = false
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
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
        archiveFileName.set("${rootProject.name}Revamp-${version}.jar".replace("SNAPSHOT", getGitHash))
        relocate("co.aikar.commands", "co.aikar.${rootProject.name.lowercase()}.acf")
        relocate("co.aikar.locales", "co.aikar.${rootProject.name.lowercase()}.locales")
        relocate("com.clanjhoo.dbhandler", "com.clanjhoo.${rootProject.name.lowercase()}.dbhandler")
        //relocate("com.zaxxer.hikari", "com.zaxxer.${rootProject.name.lowercase()}.hikari")
        include("acf-paper-*-SNAPSHOT.jar")
        include("DBHandler-*.jar")
        //include("HikariCP-*.jar")
        include("acf-core_*.properties")
        include("co/aikar/**")
        include("com/clanjhoo/**")
        include("com/zaxxer/dbhandler/hikari/**")
        include("*.yml")
        include("locales/*.yml")
    }
}

// 1)
// For >=1.20.5 when you don't care about supporting spigot
// paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

// 2)
// For 1.20.4 or below, or when you care about supporting Spigot on >=1.20.5
// Configure reobfJar to run when invoking the build task
//tasks.assemble {
//    dependsOn(tasks.reobfJar)
//}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
/*
bukkitPluginYaml {
    main = "io.papermc.paperweight.testplugin.TestPlugin"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Author")
    apiVersion = "1.21"
}
*/
