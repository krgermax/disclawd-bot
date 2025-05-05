package com.github.krgermax.data.mineworld;

import com.github.krgermax.data.biomes.Biome;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.main.Main;

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

    public Mineworld getMineworld(String guildId) {
        return mineworldMap.compute(guildId, (id, existing) -> {
            if (existing != null) {
                Main.LOGGER.info("Retrieved existing Mineworld for guild: " + id);
                return existing;
            } else {
                Main.LOGGER.info("Creating new Mineworld for guild: " + id);
                return new Mineworld(biomeList, mobList);
            }
        });
    }

    public void removeMineworld() {
        //TODO: remove by timestamp
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
