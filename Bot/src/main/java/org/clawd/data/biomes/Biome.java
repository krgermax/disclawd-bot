package org.clawd.data.biomes;

import org.clawd.data.mobs.enums.MobSubType;

import java.util.Set;

public record Biome(String name, BiomeType type, double biomeHP, String imgPath, boolean xpEnabled, Set<MobSubType> spawnableMobSubTypes) {
}
