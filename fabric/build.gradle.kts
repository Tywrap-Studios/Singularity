plugins {
    id("com.github.johnrengelman.shadow")
}

architectury {
    platformSetupLoomIde()
    fabric()
}

loom {
    accessWidenerPath.set(project(":common").loom.accessWidenerPath)
}

val common: Configuration by configurations.creating
val shadowCommon: Configuration by configurations.creating
val developmentFabric: Configuration by configurations.getting

configurations {
    compileOnly.configure { extendsFrom(common) }
    runtimeOnly.configure { extendsFrom(common) }
    developmentFabric.extendsFrom(common)
}

dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    modApi("net.fabricmc.fabric-api:fabric-api:${rootProject.property("fabric_api_version")}")
    modApi("dev.architectury:architectury-fabric:${rootProject.property("architectury_version")}")
    modApi("me.shedaniel.cloth:cloth-config-fabric:${rootProject.property("cloth_version")}")
    modApi("com.terraformersmc:modmenu:4.2.0-beta.2")

    common(project(":common", "namedElements")) {
        isTransitive = false
    }

    shadowCommon(project(":common", "transformProductionFabric")) {
        isTransitive = false
    }
}

processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") {
        expand(mapOf(
            "version" to project.version,
        ))
    }
}

tasks.shadowJar {
    configurations = listOf(shadowCommon)
    archiveClassifier.set("dev-shadow")
}

tasks.remapJar {
    injectAccessWidener.set(true)
    inputFile.set(tasks.shadowJar.get().archiveFile)
    dependsOn(tasks.shadowJar)
    archiveClassifier.set(null as String?)
}

tasks.jar {
    archiveClassifier.set("dev")
}

sourcesJar {
    val commonSources = project(":common").tasks.getByName<Jar>("sourcesJar")
    dependsOn(commonSources)
    from(commonSources.archiveFile.map { zipTree(it) })
}

components.getByName("java") {
    this.withVariantsFromConfiguration(project.configurations["shadowRuntimeElements"]) {
        skip()
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenFabric") {
            artifactId = rootProject.property("archives_base_name").toString() + "-" + project.name
            from(components["java"])
        }
    }

    repositories {

    }
}