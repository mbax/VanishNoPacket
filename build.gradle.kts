plugins {
    java
    `maven-publish`
}

repositories {
    //mavenLocal()

    maven("https://papermc.io/repo/repository/maven-public/"){
        content {
            includeGroup("io.papermc.paper")
            includeGroup("io.papermc")
        }
    }
    maven("https://jitpack.io"){
        content {
            includeGroup("com.github.MilkBowl")
        }
    }

    //maven("https://ci.ender.zone/plugin/repository/everything/")

    maven("https://repo.mikeprimm.com/"){
        content {
            includeGroup("us.dynmap")
        }
    }

    //maven("https://nexus.hc.to/content/repositories/pub_releases")

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/"){
        content {
            includeGroup("me.clip")
        }
    }

    //maven("https://repo.maven.apache.org/maven2/")

    maven("https://nexus.scarsz.me/content/groups/public/"){ //For DiscordSRV and JDA
        content {
            includeGroup("com.discordsrv")
            includeGroup("github.scarsz")
        }
    }
    maven("https://m2.dv8tion.net/releases"){ //For DiscordSRV and JDA
        content {
            includeGroup("net.dv8tion")
        }
    }
    maven("https://repo.essentialsx.net/snapshots/"){
        content {
            includeGroup("net.essentialsx")
        }
    }
    mavenCentral()

}


dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    compileOnly("io.papermc.paper:paper-api:1.18.1-R0.1-SNAPSHOT")
    compileOnly("us.dynmap:dynmap-api:3.1"){
        exclude(group= "org.bukkit", module= "bukkit")
    }
    compileOnly("net.essentialsx:EssentialsX:2.19.1-SNAPSHOT"){
        exclude(group= "org.spigotmc", module= "spigot-api")
    }
    compileOnly("com.github.MilkBowl:VaultAPI:1.7"){
        exclude(group= "org.bukkit", module= "bukkit")
    }
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("org.checkerframework:checker-qual:3.21.1")
    compileOnly("com.discordsrv:discordsrv:1.24.0"){
        exclude(group= "net.kyori", module= "adventure-text-serializer-bungeecord")
        exclude(group= "net.kyori", module= "adventure-platform-bukkit")
        exclude(group= "net.kyori", module= "adventure-text-minimessage")
    }
}

group = "org.kitteh"
version = "3.22-SNAPSHOT"
description = "VanishNoPacket"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}
