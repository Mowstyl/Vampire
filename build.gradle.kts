plugins {
    `java-library`
    alias(libs.plugins.shadowPlugin)
    alias(libs.plugins.generatePOMPlugin)
}


group = "com.clanjhoo"
version = "1.0.0-SNAPSHOT"
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
        url = uri("https://nexus.clanjhoo.com/repository/maven-public/")
        content {
            includeGroup("com.clanjhoo")
            includeGroup("us.rfsmassacre")
        }
    }
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":v1_13"))
    implementation(project(":v1_20_2"))
    implementation(project(":v1_20_3"))
    implementation(project(":v1_20_5"))
    implementation(project(":v1_20_6"))
    implementation(project(":v1_21"))
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
        relocate("org.slf4j", "org.${rootProject.name.lowercase()}.sl4fj")
        exclude("com/google/gson/**")
        exclude("META-INF/services/**")
        exclude("META-INF/versions/**")
        exclude("META-INF/maven/co.aikar/**")
        exclude("META-INF/maven/com.zaxxer/**")
        exclude("META-INF/maven/com.google.code.gson/**")
        exclude("META-INF/maven/net.jodah/**")
        exclude("META-INF/maven/org.slf4j/**")
        exclude("META-INF/maven/com.clanjhoo/dbhandler/**")
    }
}
