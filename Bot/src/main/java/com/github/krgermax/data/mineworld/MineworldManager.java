package com.github.krgermax.data.mineworld;

import com.github.krgermax.data.blocks.Block;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.main.Main;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MineworldManager {

    private static MineworldManager INSTANCE;
    private final Map<String, Mineworld> mineworldMap = new ConcurrentHashMap<>();
    private final List<Block> blockList;
    private final List<Mob> mobList;

    private static final long MINE_CACHE_EXPIRY_MINUTES = 2;
    public static final int MINE_CACHE_PERIOD_MINUTES = 2;

    private MineworldManager(List<Block> blockList, List<Mob> mobList) {
        this.blockList = blockList;
        this.mobList = mobList;
    }

    /**
     * Calls the private constructor
     *
     * @param blockList Needed to create mineworld objects later on
     * @param mobList Needed to create mineworld objects later on
     *
     * @return Mineworld instance
     */
    public static MineworldManager getInstance(List<Block> blockList, List<Mob> mobList) {
        if (INSTANCE == null) {
            INSTANCE = new MineworldManager(blockList, mobList);
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
                return new Mineworld(blockList, mobList);
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
                entry.getValue().getTimestamp().isBefore(now.minusMinutes(MINE_CACHE_EXPIRY_MINUTES)));

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

    public List<Block> getBlocks() {
        return blockList;
    }

    public List<Mob> getMobs() {
        return mobList;
    }
}
