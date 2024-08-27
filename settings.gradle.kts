plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.8.0")
}

gradle.extra["projectName"] = "VampireRevamp"
rootProject.name = "vampire"

include(
    "core",
    "v1_13",
    "v1_20_2",
    "v1_20_3",
    "v1_20_5",
    "v1_20_6",
    "v1_21"
)
