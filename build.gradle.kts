import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar


plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.clanjhoo"
version = "1.0.BETA-20"
description = "Anyone can become a vampire, but do you want to? During daytime vampires cower from sunlight. During the night the humans reach for their holy water and wooden stakes as the vampires roam the lands with inhuman strength, speed and levitation-powers. Driven by their endless bloodlust, they devour all living in their way."
java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = JavaVersion.VERSION_17

tasks {
    shadowJar.get().archiveFileName.set("${rootProject.name}Revamp-${version}.jar")
    build.get().dependsOn(shadowJar)
}

repositories {
    gradlePluginPortal()
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://maven.enginehub.org/repo/")
    }

    maven {
        url = uri("https://repo.md-5.net/content/groups/public/")
    }

    maven {
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://nexus.clanjhoo.com/repository/maven-public/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT") {
        isTransitive = false
    }
    implementation("com.clanjhoo:DBHandler:2.1.4") {
        isTransitive = false
    }
    implementation("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT") {
        isTransitive = false
    }
    implementation("com.sk89q.worldedit:worldedit-core:7.3.0-SNAPSHOT") {
        isTransitive = false
    }
    implementation("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT") {
        isTransitive = false
    }
    implementation("com.sk89q.worldguard:worldguard-core:7.1.0-SNAPSHOT") {
        isTransitive = false
    }
    implementation("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT") {
        isTransitive = false
    }
    implementation("LibsDisguises:LibsDisguises:10.0.38") {
        isTransitive = false
    }
    implementation("me.clip:placeholderapi:2.11.4") {
        isTransitive = false
    }
    implementation("com.github.MilkBowl:VaultAPI:1.7.1") {
        isTransitive = false
    }
    implementation(files("./lib/Werewolf-1.7.2-SNAPSHOT.jar"))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.processResources {
    filesMatching("**/plugin.yml") {
        expand( project.properties )
    }
}

// Configure Shadow to output with normal jar file name:
tasks.named<ShadowJar>("shadowJar").configure {
    relocate("co.aikar.commands", "co.aikar.${rootProject.name.lowercase()}.acf")
    relocate("co.aikar.locales", "co.aikar.${rootProject.name.lowercase()}.locales")
    relocate("com.clanjhoo.dbhandler", "com.clanjhoo.${rootProject.name.lowercase()}.dbhandler")
    include("acf-paper-*-SNAPSHOT.jar")
    include("DBHandler-*.jar")
    include("acf-core_*.properties")
    include("co/aikar/**")
    include("co/aikar/**")
    include("com/clanjhoo/**")
    include("org/mariadb/**")
    include("*.yml")
    include("locales/*.yml")
}

