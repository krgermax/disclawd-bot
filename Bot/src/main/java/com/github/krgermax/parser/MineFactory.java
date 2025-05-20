package com.github.krgermax.parser;

import com.github.krgermax.data.blocks.Block;
import com.github.krgermax.data.blocks.BlockType;
import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.UtilItem;
import com.github.krgermax.data.items.WeaponItem;
import com.github.krgermax.data.items.enums.ItemType;
import com.github.krgermax.data.mobs.BossMob;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.data.mobs.NormalMob;
import com.github.krgermax.data.mobs.TradeMob;
import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.mobs.enums.TradeType;

import java.io.File;
import java.util.Set;

public class MineFactory {
    /**
     * Factory method to create a utility item object
     *
     * @param uniqueID Item ID
     * @param name Name
     * @param desc Description
     * @param itemType Type
     * @param dropChance Drop chance
     * @param xpMultiplier XP-Multiplier
     * @param goldMultiplier Gold-Multiplier
     *
     * @return A utility item object
     */
    public static Item createUtilityItem(
            int uniqueID,
            String name,
            String itemEmoji,
            String desc,
            String imgPath,
            int reqLvl,
            ItemType itemType,
            double dropChance,
            double xpMultiplier,
            double goldMultiplier
    ) {
        File imgFile = validateImagePath(imgPath);
        return new UtilItem(uniqueID, name, itemEmoji, desc, imgFile, reqLvl, itemType, dropChance, xpMultiplier, goldMultiplier);
    }

    /**
     * Factory method to create a weapon item object
     *
     * @param uniqueID Unique ID
     * @param name Name
     * @param desc Description
     * @param itemType Type
     * @param dropChance Drop chance
     * @param xpMultiplier XP-Multiplier
     * @param dmgMultiplier Damage-Multiplier
     *
     * @return A weapon item object
     */
    public static Item createWeaponItem(
            int uniqueID,
            String name,
            String itemEmoji,
            String desc,
            String imgPath,
            int reqLvl,
            ItemType itemType,
            double dropChance,
            double xpMultiplier,
            double dmgMultiplier
    ) {
        File imgFile = validateImagePath(imgPath);
        return new WeaponItem(uniqueID, name, desc, itemEmoji, imgFile,  reqLvl, itemType, dropChance, xpMultiplier, dmgMultiplier);
    }

    /**
     * Factory method to create a normal mob object
     *
     * @param uniqueID Unique
     * @param name Name
     * @param desc Description
     * @param mobType Mob type
     * @param mobSubType Mob subtype
     * @param imgPath Image path
     * @param spawnChance Spawn-chance
     * @param xpDrop XP Drop
     * @param goldDrop Gold drop
     *
     * @return A normal mob object
     */
    public static Mob createNormalMob(
            int uniqueID,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance,
            double xpDrop,
            int goldDrop
    ) {
        File imgFile = validateImagePath(imgPath);
        return new NormalMob(uniqueID, name, desc, mobType, mobSubType, imgFile, spawnChance, xpDrop, goldDrop);
    }

    /**
     * Factory method to create a boss mob object
     *
     * @param uniqueID Unique
     * @param name Name
     * @param desc Description
     * @param mobType Mob type
     * @param mobSubType Mob subtype
     * @param imgPath Image path
     * @param spawnChance Spawn-chance
     * @param xpDrop XP Drop
     * @param goldDrop Gold drop
     * @param specialDrop Special drop
     * @param health Health
     *
     * @return A boss mob object
     */
    public static Mob createBossMob(
            int uniqueID,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance,
            double xpDrop,
            int goldDrop,
            boolean specialDrop,
            double health
    ) {
        File imgFile = validateImagePath(imgPath);
        return new BossMob(uniqueID, name, desc, mobType, mobSubType, imgFile, spawnChance, xpDrop, goldDrop, specialDrop, health);
    }

    /**
     * Factory method to create a trade mob object
     *
     * @param uniqueID Unique
     * @param name Name
     * @param desc Description
     * @param mobType Mob type
     * @param mobSubType Mob subtype
     * @param imgPath Image path
     * @param spawnChance Spawn-chance
     * @param tradeType Trade type
     *
     * @return A trade mob object
     */
    public static Mob createTradeMob(
            int uniqueID,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance,
            TradeType tradeType
    ) {
        File imgFile = validateImagePath(imgPath);
        return new TradeMob(uniqueID, name, desc, mobType, mobSubType, imgFile, spawnChance, tradeType);
    }

    /**
     * Factory method to create a block object
     *
     * @param name Name
     * @param blockType Block type
     * @param blockHP Block HP
     * @param imgPath Image path
     * @param xpEnabled XP enabled
     * @param spawnableMobs Spawnable mobs
     *
     * @return A block object
     */
    public static Block createBlock(
            String name,
            BlockType blockType,
            double blockHP,
            String imgPath,
            boolean xpEnabled,
            Set<MobSubType> spawnableMobs
    ) {
        File imgFile = validateImagePath(imgPath);
        return new Block(name, blockType, blockHP, imgFile, xpEnabled, spawnableMobs);
    }

    private static File validateImagePath(String imgPath) {
        File file = new File(imgPath);
        if (!file.exists()) {
            throw new IllegalArgumentException("Image path not found: " + imgPath);
        }
        return file;
    }
}
