# Disclawd
What is Disclawd?  **Disclawd** is a clicker game for Discord. It's possible to "mine" blocks and ores from a pretty [familiar game](https://www.minecraft.net/en-us), doing this grants gold, xp and allows one to buy items. Besides that watch out to slay spawning monsters!

<p align="center">
  <img src="https://github.com/clawdd/Disclawd-Bot/blob/master/Bot/src/main/resources/images/items/magic_stone.png" alt="Magic Stone" width="200" style="margin: 0 10px;"/>
  <img src="https://github.com/clawdd/Disclawd-Bot/blob/master/Bot/src/main/resources/images/items/not_a_stick.png" alt="Not a Stick" width="200" style="margin: 0 10px;"/>
  <img src="https://github.com/clawdd/Disclawd-Bot/blob/master/Bot/src/main/resources/images/items/reduvia.png" alt="Reduvia" width="200" style="margin: 0 10px;"/>
</p>

>[!NOTE]
> The bot is **guild-aware**, each server that interacts with biomes generates its own world instance. This ensures server-specific biome progress and mob spawning. (*Note: Instances are automatically cleared after 2 hours of inactivity*).

>[!CAUTION]
> While Disclawd has core gameplay features implemented and is functional in its base state, the project is not yet ready for third-party deployment. Use at your own risk. Community deployment support will be considered once the project reaches a more stable release. Visit [Disclawd Test Server](https://discord.gg/U7HjdbSEPT) to test Disclawd.

## Table of Contents
- [Commands](#commands)
- [Biomes](#biomes)
- [Mobs](#mobs)
- [Acknowledgments](#acknowledgments)

## Commands
- **`/help`** – Shows an *ephemeral* message explaining the game concept.
- **`/rank [category]`** – Shows the Top 10 players. If you aren't in the top 10, your global position appears. Supports autocomplete.
- **`/biome`** – Displays the current biome, its HP, and active users. Includes a `hit` button.
- **`/shop`** – Opens a paginated shop interface (see pixel art above).
- **`/item [item name]`** – Shows item info with `buy`/`equip` buttons. Supports autocomplete.
- **`/inventory`** – Displays a paginated inventory, main stats are shown on the first page and collected items on further.

## Biomes
- **Overworld**: Coal, Iron, Diamond, Snow, Swamp  
- **Nether**: Netherrack, Nether Quartz, Nether Gold  
- **End**: Endstone

## Mobs
- **Overworld**: Zombie, Baby Zombie, Skeleton, Skeleton Horse, Creeper, Charged Creeper, Stray, Snow Golem, Witch, Slime
- **Nether**: Piglin, Piglin & Hoglin
- **End**: Enderman, Endermite

## License
This project currently does **not** use a license. I intend to add an open-source license in the future, likely [MIT](https://opensource.org/licenses/MIT) or [Apache 2.0](https://www.apache.org/licenses/LICENSE-2.0).

## Acknowledgments
Thanks to *Sam* and *Daniel* for super early playtesting and bug-reports.

---
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![MySQL](https://img.shields.io/badge/mysql-4479A1.svg?style=for-the-badge&logo=mysql&logoColor=white)