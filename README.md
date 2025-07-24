# 🧙‍♂️ InventoryWizard

**The magical sorting assistant for your Minecraft world!** ✨

InventoryWizard is a powerful PaperMC plugin that brings intelligent, one-click inventory organization to your server. With intuitive controls and smart categorization, managing your items has never been easier!

![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Minecraft](https://img.shields.io/badge/minecraft-1.21.x-green.svg)
![PaperMC](https://img.shields.io/badge/server-PaperMC-orange.svg)

## 🌟 Features

### ✨ **Intelligent Sorting**
- **Smart categorization** - Items are grouped logically (weapons, tools, food, blocks, etc.)
- **Hotbar optimization** - Weapons and tools prioritized for PvP/survival gameplay
- **Stack consolidation** - Automatically combines partial stacks
- **Metadata preservation** - Maintains enchantments, names, and item data

### 🖱️ **Multiple Control Methods**
- **Shift+Right Click** - Sort inventories, chests, and hotbar
- **Double Click** - Alternative hotbar sorting
- **Commands** - `/iwiz` for programmatic sorting

### 🎒 **Comprehensive Coverage**
- **Player Inventory** - Organize your main storage
- **Hotbar** - Optimized for quick access to combat/survival items
- **Chests** - Keep your storage containers tidy
- **All-in-one** - Sort everything simultaneously

## 📥 Installation

1. **Download** the latest `InventoryWizard-x.x.x.jar` from the [releases page](https://github.com/VanishingTacos/inventorywizard/releases)
2. **Place** the JAR file in your server's `plugins` folder
3. **Restart** your server
4. **Configure** the plugin using `plugins/InventoryWizard/config.yml` (optional)

### Requirements
- **Minecraft**: 1.21.x
- **Server**: PaperMC, Spigot, or compatible fork
- **Java**: 17 or higher

## 🎮 Usage Guide

### **Control Scheme Overview**
| Action | Location | Permission Required | Result |
|--------|----------|-------------------|---------|
| **Shift+Right Click** | Hotbar (slots 0-8) | `inventorywizard.all` | Sort inventory + hotbar |
| **Shift+Right Click** | Hotbar (slots 0-8) | `inventorywizard.hotbar` | Sort hotbar only |
| **Shift+Right Click** | Main Inventory (slots 9-35) | `inventorywizard.inventory` | Sort main inventory |
| **Shift+Right Click** | Chest | `inventorywizard.chest` | Sort chest contents |
| **Double Click** | Hotbar (slots 0-8) | `inventorywizard.hotbar` | Sort hotbar only |

### **Sorting Your Inventory**
- Open your inventory (`E`)
- **Shift+Right Click** any slot in your main inventory area (slots 9-35)
- Watch the magic happen! ✨

### **Sorting Your Hotbar** 
- **Method 1**: **Shift+Right Click** any hotbar slot (0-8) with `inventorywizard.hotbar` permission
- **Method 2**: **Double Click** any hotbar slot
- **Method 3**: Use command `/iwiz hotbar`

### **Sorting Chests**
- Open any chest
- **Shift+Right Click** any slot in the chest
- The entire chest will be organized!

### **Sorting Everything**
- **Shift+Right Click** in your hotbar (slots 0-8) with `inventorywizard.all` permission
- Both your inventory and hotbar will be sorted!

## 🧙‍♂️ Commands

| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/iwiz` | `/inventorywizard`, `/sort` | Base command | `inventorywizard.use` |
| `/iwiz hotbar` | `/iwiz hb` | Sort hotbar only | `inventorywizard.hotbar` |
| `/iwiz inventory` | `/iwiz inv` | Sort inventory only | `inventorywizard.inventory` |
| `/iwiz all` | `/iwiz both` | Sort everything | `inventorywizard.all` |

## 🔐 Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `inventorywizard.use` | Basic plugin access | `true` |
| `inventorywizard.chest` | Sort chests | `true` |
| `inventorywizard.inventory` | Sort player inventory | `true` |
| `inventorywizard.hotbar` | Sort hotbar | `true` |
| `inventorywizard.all` | Sort everything at once | `true` |

### Permission Groups
```yaml
# Example permissions setup
groups:
  default:
    permissions:
      - inventorywizard.use
      - inventorywizard.inventory
      - inventorywizard.hotbar
  
  vip:
    permissions:
      - inventorywizard.*  # All permissions
```

## ⚙️ Configuration

The plugin generates a `config.yml` file with the following options:

```yaml
# InventoryWizard Configuration
settings:
  # Play magical sound effects when sorting
  play-sound: true
  
  # Sound settings for the wizard's magic
  sound:
    type: UI_BUTTON_CLICK
    volume: 0.5
    pitch: 1.2

  # Sorting method: "TYPE" for smart categorization or "ALPHABETICAL" 
  sort-method: "TYPE"

# Enable/disable magical features
features:
  chest-sorting: true
  inventory-sorting: true
  hotbar-sorting: true
  show-messages: true

# Hotbar sorting priorities (optimized for PvP/survival)
hotbar-priorities:
  weapons: 100        # Swords, bows, crossbows first
  tools: 200          # Pickaxes, axes, shovels second  
  food: 300           # Sustenance third
  building-blocks: 400 # Quick-build materials fourth
  utility-items: 500  # Ender pearls, buckets, torches
  other: 999          # Everything else
```

## 📋 Sorting Categories

### **Inventory Sorting Order**
1. **Stone Types** - Stone, granite, cobblestone, deepslate, etc.
2. **Earth Types** - Dirt, sand, gravel, clay, concrete, etc.
3. **Wood Types** - Logs, planks, leaves, saplings
4. **Ores & Metals** - Iron blocks, diamond blocks, etc.
5. **Other Blocks** - Remaining building materials
6. **Tools** - Pickaxes, axes, shovels, shears, etc.
7. **Weapons** - Swords, bows, arrows, tridents
8. **Armor** - Helmets, chestplates, boots, shields
9. **Food** - All edible items
10. **Redstone** - Redstone components and mechanisms
11. **Transportation** - Boats, minecarts, saddles
12. **Decoration** - Flowers, carpets, banners, etc.
13. **Miscellaneous** - Everything else

### **Hotbar Sorting Order** (Optimized for PvP/Survival)
1. **Weapons** - Combat items for quick access
2. **Tools** - Essential survival tools
3. **Food** - Health restoration
4. **Building Blocks** - Cobblestone, dirt for quick building
5. **Utility Items** - Ender pearls, buckets, torches
6. **Other Items** - Miscellaneous

## 🔧 Building from Source

```bash
# Clone the repository
git clone https://github.com/VanishingTacos/inventorywizard.git
cd inventorywizard

# Build with Maven
mvn clean package

# The JAR will be in target/InventoryWizard-1.0.0.jar
```

### Development Requirements
- Java 17+
- Maven 3.6+
- PaperMC API 1.21.4+

## 🤝 Contributing

Contributions are welcome! Please feel free to submit pull requests, report bugs, or suggest features.

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature-name`
3. **Commit** your changes: `git commit -am 'Add some feature'`
4. **Push** to the branch: `git push origin feature-name`
5. **Submit** a pull request

## 📜 Changelog

### Version 1.0.1
- 🔧 **Fixed control scheme** - Removed middle click, improved Shift+Right Click logic
- 🎯 **Enhanced hotbar sorting** - Priority-based permission system for combined sorting
- 🛠️ **Code optimization** - Eliminated duplicate event handlers and improved reliability
- 📝 **Updated documentation** - Clearer instructions and permission requirements

### Version 1.0.0
- ✨ Initial release
- 🧙‍♂️ Smart inventory sorting with type-based categorization
- 🎯 Hotbar optimization for PvP/survival gameplay
- 📦 Chest organization support
- 🖱️ Multiple control methods (click-based and commands)
- 🔊 Audio feedback and magical messages
- ⚙️ Comprehensive configuration options

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **PaperMC Team** for the excellent server software
- **Minecraft Community** for inspiration and feedback
- **Plugin Developers** who paved the way for quality Bukkit plugins

---

## ☕ Support InventoryWizard

If you enjoy using InventoryWizard and want to support its development, you can buy me a coffee!

<a href="https://buymeacoffee.com/vanishingtacos" target="_blank">
  <img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" width="150" >
</a>


**Made with** ❤️ **and** ✨ **for the Minecraft community**

*Transform your chaotic inventory into organized perfection with InventoryWizard!* 🧙‍♂️
