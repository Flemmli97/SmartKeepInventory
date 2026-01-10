# Smart Keep Inventory
[![](http://cf.way2muchnoise.eu/full_1424832_Forge_%20.svg)![](http://cf.way2muchnoise.eu/versions/1424832.svg)](https://www.curseforge.com/minecraft/mc-mods/smart-keep-inventory)  
[![](https://img.shields.io/modrinth/dt/qeGAjqzW?logo=modrinth&label=Modrinth)![](https://img.shields.io/modrinth/game-versions/qeGAjqzW?logo=modrinth&label=Latest%20for)](https://modrinth.com/mod/smart-keep-inventory)  
[![Discord](https://img.shields.io/discord/790631506313478155?color=0a48c4&label=discord)](https://discord.gg/8Cx26tfWNs)

A mod that allows enabling keep inventory only under certain conditions.

To use this mod as a dependency add the following snippet to your build.gradle:  
```groovy
repositories {
    maven {
        name = "Flemmli97"
        url "https://maven.blazing-coop.net/releases"
    }
}

dependencies {    
    //Fabric/Loom==========    
    modImplementation("io.github.flemmli97:smart_keep_inventory:${minecraft_version}-${mod_version}-${mod_loader}")
    
    //NeoForge==========    
    implementation("io.github.flemmli97:smart_keep_inventory:${minecraft_version}-${mod_version}-${mod_loader}")
}
```
