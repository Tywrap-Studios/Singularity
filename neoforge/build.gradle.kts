plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentNeoForge: Configuration by configurations.getting

configurations {
//    common {
//        canBeResolved = true
//        canBeConsumed = false
//    }
//    compileClasspath.extendsFrom (common)
//    runtimeClasspath.extendsFrom (common)
//    developmentNeoForge.extendsFrom (common)
//
//    // Files in this configuration will be bundled into your mod using the Shadow plugin.
//    // Don"t use the `shadow` configuration from the plugin itself as it"s meant for excluding files.
//    shadowCommon {
//        canBeResolved = true
//        canBeConsumed = false
//    }
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentNeoForge.extendsFrom(common)
}

repositories {
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    neoForge ("net.neoforged:neoforge:${rootProject.property("neoforge_version")}")

    modApi("dev.architectury:architectury-forge:${rootProject.property("architectury_version")}")
    modApi("me.shedaniel.cloth:cloth-config-forge:${rootProject.property("cloth_version")}")

    common(project(":common", "namedElements")) {
        isTransitive = false
    }
    
    shadowCommon(project(":common", "transformProductionForge")) {
        isTransitive = false
    }
}

tasks.processResources {
    inputs.property("version", project.version)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(mapOf(
            "version" to project.version,
        ))
    }
}

tasks.shadowJar {
    exclude("fabric.mod.json")

    configurations = listOf(shadowCommon)
    archiveClassifier.set("all")
    archiveVersion.set("${project.version}+neoforge-${rootProject.property("minecraft_version")}")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier.set(null as String?)
}
