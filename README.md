# Irradiated Wastes (Forge 1.12.2)

Adds the "Irradiated Wastes" biome with four procedural structures:
underground bunker (2 loot chests), ruined cooling tower with a green pool,
barrel field with a hidden cache, and a crater with a supply crate.
Players get Poison/Weakness/Hunger in the biome unless wearing a full set of armor
whose registry name contains "hazmat" (edit PROTECTIVE_KEYWORDS in RadiationHandler).

## Build
1. Download the Forge 1.12.2 MDK (14.23.5.2860) and unzip it.
2. Copy this project's `src/` folder into the MDK (merge, keep MDK's build.gradle and gradlew).
   In the MDK's build.gradle set `group`, `archivesBaseName` and keep mappings `stable_39`.
3. `gradlew setupDecompWorkspace` (first time), then `gradlew build`.
4. The jar appears in `build/libs/`. Drop it in `mods/` next to the HBM jar.

## Test
`gradlew runClient`, create a world, then run `/wastes` to teleport to the nearest biome.

## Tuning
- Biome frequency: weight in `ModBiomes` (12). Higher = more common.
- Structure frequency: `CHUNK_CHANCE` in `WastelandStructures` (10 = ~1 in 10 chunks in the biome).
- Loot: `assets/irradiatedwastes/loot_tables/chests/*.json` (add HBM items by registry name, e.g. "hbm:...").

## Build without installing anything (GitHub)
Create a free GitHub repo, upload the contents of this folder (keep `.github/`), open the
Actions tab, run "Build mod jar", then download the `irradiatedwastes-jar` artifact.
