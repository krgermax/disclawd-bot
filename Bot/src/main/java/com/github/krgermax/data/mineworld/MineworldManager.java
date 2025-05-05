package com.github.krgermax.data.mineworld;

import com.github.krgermax.data.biomes.Biome;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MineworldManager {

    private static MineworldManager INSTANCE;
    private final Map<String, Mineworld> mineworldMap = new ConcurrentHashMap<>();
    private final List<Biome> biomeList;
    private final List<Mob> mobList;

    private MineworldManager(List<Biome> biomeList, List<Mob> mobList) {
        this.biomeList = biomeList;
        this.mobList = mobList;
    }

    /**
     * Calls the private constructor
     *
     * @param biomeList Needed to create mineworld objects later on
     * @param mobList Needed to create mineworld objects later on
     *
     * @return Mineworld instance
     */
    public static MineworldManager getInstance(List<Biome> biomeList, List<Mob> mobList) {
        if (INSTANCE == null) {
            INSTANCE = new MineworldManager(biomeList, mobList);
        }
        return INSTANCE;
    }

    /**
     * This method returns a mineworld by either retrieving it from the concurrent {@link #mineworldMap}
     * using the provided guild id, or it creates a new mineworld instance and maps it to the given guild id
     *
     * @param guildId The guild id used to retrieve or create a new map entry
     *
     * @return The mineworld corresponding to the provided guild id
     */
    public Mineworld getMineworld(String guildId) {
        return mineworldMap.compute(guildId, (id, existing) -> {
            if (existing != null) {
                Main.LOGGER.info("Retrieved existing Mineworld for guild: " + id);
                existing.setTimestamp(LocalDateTime.now());
                return existing;
            } else {
                Main.LOGGER.info("Creating new Mineworld for guild: " + id);
                return new Mineworld(biomeList, mobList);
            }
        });
    }

    /**
     * This method cleanups the mineworld map by removing all mineworld map entries for which the lifetime of the
     * associated mineworld is expired
     */
    public void cleanupCache() {
        int before = mineworldMap.size();
        LocalDateTime now = LocalDateTime.now();

        mineworldMap.entrySet().removeIf(entry ->
                entry.getValue().getTimestamp().isBefore(now.minusMinutes(Constants.MINE_CACHE_EXPIRY_MINUTES)));

        Main.LOGGER.info("Mineworld cache cleaned: " + before + " -> " + mineworldMap.size());
    }

    /**
     * Gets a mob from the mob List by a mob ID
     *
     * @param id The mob ID we search with
     * @return The mob matching the mob ID, if not found null is returned
     */
    public Mob getMobByID(int id) {
        for (Mob mob : mobList) {
            if (mob.getID() == id)
                return mob;
        }
        return null;
    }

    public List<Biome> getBiomes() {
        return biomeList;
    }

    public List<Mob> getMobs() {
        return mobList;
    }
}
