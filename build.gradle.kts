plugins {
    // https://projects.neoforged.net/neoforged/ModDevGradle
    id("net.neoforged.moddev") version "2.0.141"
    idea
    id("me.modmuss50.mod-publish-plugin") version "1.1.0"
}

version = mod["version"]
group = mod["group"]
base.archivesName = mod["id"]

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

neoForge {
    version = neo["version"]

    validateAccessTransformers = true

    parchment {
        mappingsVersion = project.parchment["mappingsVersion"]
        minecraftVersion = project.parchment["minecraftVersion"]
    }

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            logLevel = org.slf4j.event.Level.DEBUG
        }

        create("client") {
            client()
            devLogin = true
            jvmArgument("-XX:+IgnoreUnrecognizedVMOptions")
            jvmArgument("-XX:+AllowEnhancedClassRedefinition")
            systemProperty("neoforge.enabledGameTestNamespaces", mod["id"])
        }

        create("server") {
            server()
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", mod["id"])
        }

        create("gameTestServer") {
            type = "gameTestServer"
            systemProperty("neoforge.enabledGameTestNamespaces", mod["id"])
        }

        create("data") {
            data()
            programArguments.addAll(
                "--mod", mod["id"],
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
    }

    mods {
        create(mod["id"]) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

val localRuntime: Configuration by configurations.creating

configurations {
    runtimeClasspath {
        extendsFrom(localRuntime)
    }
}

repositories {
    mavenLocal()
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

dependencies {
    compileOnly("maven.modrinth:buzzier-bees:${deps["buzzier_bees"]}")
    compileOnly("maven.modrinth:blueprint:${deps["blueprint"]}")
}

tasks.withType<ProcessResources>().configureEach {
    val replaceProperties = mapOf(
        "minecraft_version" to mc["version"],
        "minecraft_version_range" to mc["versionRange"],
        "neo_version" to neo["version"],
        "neo_version_range" to neo["versionRange"],
        "loader_version_range" to neo["loaderVersionRange"],
        "mod_id" to mod["id"],
        "mod_name" to mod["name"],
        "mod_license" to mod["license"],
        "mod_version" to mod["version"],
        "mod_authors" to mod["authors"],
        "mod_description" to mod["description"]
    )

    inputs.properties(replaceProperties)

    filesMatching("META-INF/neoforge.mods.toml") {
        expand(replaceProperties)
    }
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}

publishMods {
    type = STABLE
    file = tasks.jar.map { it.archiveFile.get() }
    changelog = provider { rootProject.file("changelog.md").readText() }

    version = mod["version"]
    displayName = "${mod["name"]} ${mod["version"]}"
    modLoaders.add("neoforge")

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_API_KEY")
        projectId = "1221222"
        projectSlug = "mixed-litter"
        minecraftVersions.add(mc["version"])

        clientRequired = true
        serverRequired = true
    }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = "cU1kDASI"
        minecraftVersions.add(mc["version"])
    }
}
