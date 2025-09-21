import xyz.jpenilla.resourcefactory.bukkit.BukkitPluginYaml

plugins {
    `java-library`
    id("xyz.jpenilla.resource-factory-bukkit-convention") version "1.3.0" // Generates plugin.yml based on the Gradle config
}

group = "club.mcpvp.consistentdurability"
version = "1.0.0"
description = "Removes infinite elytra trick and RNG from Unbreaking"

java {
    // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
        // See https://openjdk.java.net/jeps/247 for more information.
        options.release = 21
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
    }
}

// Configure plugin.yml generation
// - name, version, and description are inherited from the Gradle project.
bukkitPluginYaml {
    main = "club.mcpvp.consistentdurability.ConsistentDurability"
    load = BukkitPluginYaml.PluginLoadOrder.STARTUP
    authors.add("Jyguy")
    apiVersion = "1.21.4"
}
