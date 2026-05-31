# TotemCounterV2

<div align="center">

![Banner](src/main/resources/assets/totemcounterv2/banner.png)

![Version](https://img.shields.io/badge/version-2.4.0-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21+-green.svg)
![Fabric](https://img.shields.io/badge/mod%20loader-fabric-1976d2?logo=data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMjQiIGhlaWdodD0iMjQiIHZpZXdCb3g9IjAgMCAyNCAyNCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHBhdGggZD0iTTEyIDJMMTMuMDkgOC4yNkwyMCA5TDEzLjA5IDE1Ljc0TDEyIDIyTDEwLjkxIDE1Ljc0TDQgOUwxMC45MSA4LjI2TDEyIDJaIiBmaWxsPSIjMTk3NkQyIi8+Cjwvc3ZnPg==)
![Java](https://img.shields.io/badge/java-21-orange.svg)

A clean, interactive, and highly customizable totem pop counter for Crystal PVP, built for Fabric 1.21.

</div>

## Overview

TotemCounterV2 tracks and displays totem pops for players around you. It's designed to be simple, unobtrusive, and highly customizable, featuring a fully drag-and-drop HUD and built-in custom audio options.

## Dependencies: https://github.com/m4ssivee/m4lib
You must have m4lib to use all functions of TotemCounterV2.

## Features

- **Interactive HUD:** Press `K` in-game to enter Edit Mode. You can drag the HUD anywhere on your screen and use the corner handles to resize.
- **Custom Audio Elements:** Choose from multiple custom pop sounds (Note Blocks, Level Up, Explosion, etc.) and tweak volume/pitch. Automatically mutes the vanilla vanilla sound when using a custom sound.
- **Filtering & Scope:** Configure max display players, maximum tracking distance, and optionally hide your own totem pops from the list.
- **Visual Customization:** Press `H` or `L` to open the configuration menu. You can easily tweak font colors, background colors, borders, scale, and padding formats.
- **Commands:** 
  - `/resetcounter` or `/resetscoreboard` to clear all current tracked pops.
  - `/imperial` for access to our Discord community.

## Installation

1. Download the latest release from [Modrinth](https://modrinth.com/mod/totemcounterv2) or the [GitHub Releases](https://github.com/m4ssivee/totemcounterv2/releases).
2. Install [Fabric Loader](https://fabricmc.net/use/) (0.15.11 or newer) and [Fabric API](https://modrinth.com/mod/fabric-api) for Minecraft 1.21.
3. Drop the `TotemCounterV2` jar into your `.minecraft/mods` folder.
*(Installing [Mod Menu](https://modrinth.com/mod/modmenu) is recommended for easier access to settings.)*

## Building from source

Requirements:
- Java 21+

```bash
git clone https://github.com/m4ssivee/totemcounterv2.git
cd totemcounterv2
./gradlew build
```
The compiled jar will be located in `build/libs/`.

---

**License:** MIT  
**Author:** m4ssivee  
*Made for the Crystal PVP community.*
