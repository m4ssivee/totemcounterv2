# 🎮 TotemCounterV2

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21+-green.svg)](https://www.minecraft.net/)
[![Fabric](https://img.shields.io/badge/Mod%20Loader-Fabric-blue.svg)](https://fabricmc.net/)
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://adoptium.net/)

**Developer:** [m4ssive](https://github.com/m4ssivee) (m4main)  
**Version:** 2.0.0  
**License:** MIT  
**Minecraft Version:** 1.21.x (All 1.21 versions)  
**Mod Loader:** Fabric  
**Java Version:** 21+

## 📋 Description

TotemCounterV2 is a professional Fabric mod for Crystal PVP players. It accurately tracks totem pops with a fully customizable, mouse-interactive HUD featuring transparent backgrounds and smooth animations.

## ✨ Features

- ✅ **100% Accurate Counting** - Entity status packet detection with duplicate filtering
- ✅ **Mouse-Interactive HUD** - Left-click drag to move, right-click corners to resize
- ✅ **Transparent Backgrounds** - 40% opacity for minimal screen obstruction
- ✅ **Preset Soft Colors** - Beautiful pastel color palette
- ✅ **Auto-Reset on Death** - Counters reset when player dies
- ✅ **Customizable Hotkeys** - Choose your preferred config key (L/J/K/M)
- ✅ **Edit Mode** - Press **Ş** key for quick HUD editing
- ✅ **Mod Menu Integration** - Full configuration through Mod Menu
- ✅ **Totem Icons** - Visual totem items in HUD
- ✅ **Distance Filter** - Show only nearby players
- ✅ **Auto Sorting** - Most pops displayed first
- ✅ **Performance Optimized** - Minimal CPU/RAM impact
- ✅ **Open Source** - MIT license

## 📥 Download

Download the latest release from [Releases](../../releases) page:

- **[totemcounterv2-2.0.0.jar](../../releases)** - Compatible with all Minecraft 1.21.x versions

**Single JAR for all 1.21 versions!** Works on 1.21, 1.21.1, 1.21.2, 1.21.3, 1.21.4, and future 1.21.x releases.

## 🚀 Installation

### Requirements

1. **Minecraft** 1.21.x (any 1.21 version)
2. **[Fabric Loader](https://fabricmc.net/use/)** 0.15.11+
3. **[Fabric API](https://modrinth.com/mod/fabric-api)** (latest for your MC version)
4. **Java 21** ([Download Adoptium JDK 21](https://adoptium.net/temurin/releases/?version=21))

### Steps

1. Install Fabric Loader for Minecraft 1.21.x
2. Download latest Fabric API from Modrinth/CurseForge
3. Download **totemcounterv2-2.0.0.jar** from [Releases](../../releases)
4. Place both JAR files in `.minecraft/mods` folder
5. Launch Minecraft with Fabric profile
6. First launch: **Choose your config hotkey** (L/J/K/M)

## 🎮 Usage

### First Time Setup

On first launch, you'll be prompted to choose your **config menu hotkey**:
- **L** (recommended)
- **J**
- **K**
- **M**

### Basic Usage

1. Join a server or world
2. HUD automatically displays when players pop totems
3. Counters reset automatically when players die
4. Press your **config key** to open settings
5. Press **Ş** key to enter Edit Mode

### Hotkeys

- **L/J/K/M** (your choice): Open configuration menu
- **Ş**: Toggle Edit Mode (drag & resize HUD)
- **ESC**: Exit Edit Mode and save

### Edit Mode

**Activate:** Press **Ş** key or click "EDIT MODE" button in settings

**Controls:**
- **Left-Click Drag HUD**: Move position
- **Right-Click Drag Corners**: Resize (yellow handles)
- **Ş or ESC**: Save and exit

### Settings Menu

**Open:** Press your config key (default: L) or via **Mod Menu**

#### Color Settings
Choose from beautiful preset colors (no hex codes needed!):

**Text Colors:**
- Soft White, Soft Pink, Soft Blue, Soft Green
- Soft Yellow, Soft Purple, Soft Orange, Soft Mint

**Background Colors** (40% opacity - transparent!):
- Dark BG, Gray BG, Blue BG, Green BG, Purple BG, Very Clear BG (20%)

**Border Colors:**
- White Border, Gold Border, Aqua Border, Red Border, Lime Border

#### Display Options
- **Show Background**: Background on/off
- **Show Border**: Border on/off
- **Show Icon**: Show/hide totem icons ⭐
- **Show Self**: Display your own totem count
- **Show Only Nearby**: Distance filter
- **Nearby Distance**: How many blocks (default: 50)
- **Max Players**: Maximum players shown (default: 10)

## 🛠️ Building from Source

### Prerequisites

- **Java 21** ([Download Adoptium JDK 21](https://adoptium.net/temurin/releases/?version=21))
- **Git** (optional, for cloning)

### Build Instructions

#### Windows

```powershell
# Clone the repository (or download ZIP)
git clone https://github.com/m4ssivee/totemcounterv2.git
cd totemcounterv2

# Build for Minecraft 1.21.4 (default)
.\gradlew.bat build

# Output: build/libs/totemcounterv2-2.0.0.jar
```

#### Linux/Mac

```bash
# Clone the repository (or download ZIP)
git clone https://github.com/m4ssivee/totemcounterv2.git
cd totemcounterv2

# Make gradlew executable
chmod +x gradlew

# Build for Minecraft 1.21.4 (default)
./gradlew build

# Output: build/libs/totemcounterv2-2.0.0.jar
```

### Building for Different Versions

To build for Minecraft 1.21, edit `gradle.properties` and change:

```properties
minecraft_version=1.21
yarn_mappings=1.21+build.9
loader_version=0.15.11
fabric_version=0.100.1+1.21
```

Then run the build command again.

## 📊 Technical Details

### Performance

- **CPU Usage**: <0.5%
- **RAM Usage**: ~2-3 MB
- **FPS Impact**: <2 FPS

### Compatibility

✅ **Compatible with:**
- Sodium
- Iris Shaders
- Mod Menu
- Lithium
- Starlight
- Most Fabric optimization mods

## 🔒 Security & Fair Play

### ✅ TotemCounterV2:
- Information mod only
- Shows visible events
- Supports fair play

### ❌ NOT:
- Not a cheat/hack
- No automation
- No game mechanic changes

**⚠️ WARNING:** Server rules may vary. Check server rules before using!

## 📁 Project Structure

```
TotemCounterV2/
├── src/main/java/com/m4ssive/totemcounterv2/
│   ├── TotemCounterV2Mod.java
│   ├── TotemTracker.java
│   ├── config/ModConfig.java
│   ├── gui/TotemHud.java (⭐ Icon support)
│   ├── gui/ConfigScreen.java
│   └── mixin/ClientPlayNetworkHandlerMixin.java
├── src/main/resources/
│   ├── fabric.mod.json
│   └── assets/totemcounterv2/
│       ├── icon.png (⭐ Mod icon)
│       └── lang/
├── gradle/
├── build.gradle
├── gradle.properties
└── README.md
```

## 🐛 Troubleshooting

### Mod Not Loading
- Is Fabric Loader 0.15.11+ installed?
- Is Fabric API in mods folder?
- Is Java 21+ installed?

### HUD Not Showing
- Open settings with Right Shift
- Check position values
- Try toggling "Show Icon" option

### Icons Not Showing
- Config must have `showIcon: true`
- May conflict with Minecraft texture packs

## 📝 Changelog

### v2.0.0 (2025-10-26)
- 🎉 Rebranded as **TotemCounterV2**
- ✨ **Mouse-interactive HUD** - Drag and resize with mouse
- ✨ **Preset soft colors** - Beautiful color palette
- ✨ **Mod Menu integration** - Configure from mod menu
- ✨ **Accurate counting** - Based on uku3lig's method
- ✨ **Hotkey changed** - Press L to open settings
- ✨ Totem icon support with toggle
- 🔧 Major code optimization
- 🔧 Improved HUD rendering
- 📦 Updated for 1.21.x (1.21 - 1.21.4)
- ✅ Multi-version support

### v1.0.0 (2025-10-26)
- Initial release (TotemCounter)

## 🤝 Contributing

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📞 Contact

- **GitHub:** [@m4ssivee](https://github.com/m4ssivee)
- **Discord:** yagiwz
- **Issues:** [Report Bug](../../issues)

## 📜 License

```
MIT License
Copyright (c) 2025 m4ssive

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

Full license: [LICENSE](LICENSE)

## 🙏 Credits

- **uku3lig** - Original totem counting method inspiration ([totemcounter](https://github.com/uku3lig/totemcounter))
- **Fabric Team** - Mod loader framework
- **Minecraft Community** - Support and feedback
- **Crystal PVP Players** - Testing and suggestions

---

<div align="center">

**⚡ Dominate Crystal PVP with TotemCounterV2! ⚡**

Made with ❤️ by **m4ssive** | v2.0.0

[⬆ Back to top](#-totemcounterv2)

</div>
