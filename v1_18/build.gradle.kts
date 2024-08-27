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
    }
    mavenCentral()
}

dependencies {
    compileOnly(project(":core"))
    compileOnly("org.spigotmc:spigot-api:1.18-R0.1-SNAPSHOT")
    //compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

tasks {
    compileJava {
        options.encoding = Charsets.UTF_8.name()
    }
}
