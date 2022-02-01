import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("maven-publish")
//    id ("com.github.johnrengelman.shadow") version "7.0.0"

    id("org.spongepowered.gradle.plugin") version "2.0.1"
    id("org.cadixdev.licenser") version "0.6.1"

    id("org.jetbrains.kotlin.jvm") version "1.5.10"
}

allprojects {
    apply(plugin = "java-library")
    apply(plugin = "org.cadixdev.licenser")

    val javaTarget = 8
    java {
        sourceCompatibility = JavaVersion.toVersion(javaTarget)
        targetCompatibility = JavaVersion.toVersion(javaTarget)
    }

    repositories {
//        mavenLocal()
        mavenCentral()
        maven(uri("https://repo.spongepowered.org/maven"))
        maven(uri("https://jitpack.io"))
    }

    license {
        header.set(resources.text.fromFile(rootProject.file("LICENSE")))

        include("**/*.java", "**/*.kt")

        newLine.set(false)
    }
}

base.archivesName.set("BoxOUtils")

val kotlinVersion: String by properties
val spongeApiVersion: String by properties
val alwaysIncludedIntegrations: Set<String> = (properties["alwaysIncludedIntegrations"] as? String)?.let {
    if (it.isBlank()) emptySet() else it.split(',').toSet()
} ?: emptySet()

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    testImplementation("org.spongepowered:spongeapi:${spongeApiVersion}")
    for (integration in alwaysIncludedIntegrations) {
        testImplementation(project("integrations:$integration"))
    }

    testRuntimeOnly("org.apache.logging.log4j:log4j-core:2.8.1")
}

tasks.processResources {
    val props = mapOf("version" to version, "spongeApiVersion" to spongeApiVersion)
    inputs.properties(props)
    // Do not try to expand service files, they may contain a dollar sign to mark inner classes
    filesNotMatching("META-INF/services/*") {
        expand(props)
    }
}

sponge {
    apiVersion(spongeApiVersion)
    license("MIT")
    loader {
        name(PluginLoaders.JAVA_PLAIN)
        version("1.0")
    }
    plugin("box-o-utils") {
        displayName("Box O\" Utils")
        entrypoint("io.github.rednesto.bou.BoxOUtils")
        description("Control blocks and mobs loots, right-click to harvest and more")
        links {
            homepage("https://ore.spongepowered.org/RedNesto/Box-O%27-Utils")
            source("https://github.com/RedNesto/BoxOUtils")
            issues("https://github.com/RedNesto/BoxOUtils/issues")
        }
        contributor("RedNesto") {
            description("Author")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

//build.dependsOn shadowJar
//shadowJar {
//    archiveClassifier.set(null)
//    configurations = [] // We do not need to include anything else than the integrations
//    withIntegrations { proj -> from proj.jar.outputs.files }
//}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("bou.is_testing", "true")
}

val sourcesJar by tasks.registering(Jar::class) {
    dependsOn("classes")
    archiveClassifier.set("sources")
    from(sourceSets["main"].allSource)
    withIntegrations { proj -> from(proj.sourceSets["main"].allSource) }
}

val javadocJar by tasks.registering(Jar::class) {
    dependsOn("javadoc")
    archiveClassifier.set("javadoc")
    from(tasks.javadoc.get().destinationDir)
    withIntegrations { proj -> from(proj.tasks.getByName("javadoc", Javadoc::class).destinationDir) }
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            groupId = group as String
            artifactId = "box-o-utils"
            version = version

            artifact(tasks.jar)
            artifact(sourcesJar)
            artifact(javadocJar)
        }
    }
}

fun withIntegrations(action: (Project) -> Unit) {
    try {
        for (path in project("integrations").subprojects) {
            action(path)
        }
    } catch (ignore: UnknownProjectException) {
    }
}

if (project.file("local.gradle").exists()) {
    // in case anyone wants to have additional tasks
    apply(from = "local.gradle")
}
