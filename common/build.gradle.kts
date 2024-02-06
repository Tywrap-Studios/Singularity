dependencies {
    modImplementation("net.fabricmc:fabric-loader:${rootProject.property("fabric_loader_version")}")
    
    modApi("dev.architectury:architectury:${rootProject.property("architectury_version")}")
    modApi("me.shedaniel.cloth:cloth-config:${rootProject.property("cloth_version")}")
}

loom {
    accessWidenerPath.set(file("src/main/resources/singularity.accesswidener"))
}

architectury {
    common("fabric", "forge")
}

publishing {
    publications {
        create<MavenPublication>("mavenCommon") {
            artifactId = rootProject.property("archives_base_name").toString()
            from(components["java"])
        }
    }

    repositories {

    }
}
