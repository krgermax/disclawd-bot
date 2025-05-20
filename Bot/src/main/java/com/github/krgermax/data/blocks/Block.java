package com.github.krgermax.data.blocks;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.WeaponItem;
import com.github.krgermax.data.items.enums.ItemType;
import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.io.File;
import java.util.Set;

public class Block {
    private final String name;
    private final BlockType type;
    private final double blockHP;
    private double currentHP;
    private double adjustableFullHP;
    private final File imgFile;
    private final boolean xpEnabled;
    private final Set<MobSubType> spawnableMobSubTypes;

    public Block(String name, BlockType type, double blockHP, File imgFile, boolean xpEnabled, Set<MobSubType> spawnableMobSubTypes) {
        this.name = name;
        this.type = type;
        this.blockHP = blockHP;

        this.currentHP = blockHP;
        this.adjustableFullHP = blockHP;

        this.imgFile = imgFile;

        this.xpEnabled = xpEnabled;
        this.spawnableMobSubTypes = spawnableMobSubTypes;
    }

    public String getName() {
        return name;
    }

    public BlockType getType() {
        return type;
    }

    public double getTrueHP() {
        return blockHP;
    }

    public double getCurrentHP() {
        return currentHP;
    }

    public double getAdjustableFullHP() {
        return adjustableFullHP;
    }

    public File getImgFile() {
        return imgFile;
    }

    public boolean isXpEnabled() {
        return xpEnabled;
    }

    public Set<MobSubType> getSpawnableMobSubTypes() {
        return spawnableMobSubTypes;
    }

    public void setCurrentHP(double currentHP) {
        this.currentHP = currentHP;
    }

    public void setAdjustableFullHP(double adjustableFullHP) {
        this.adjustableFullHP = adjustableFullHP;
    }

    /**
     * Calculates the damage done to a block by a user and applies it to
     * the current HP of the current block
     *
     * @param equippedItem The equipped user item
     */
    public void damage(Item equippedItem) {
        double dmgMult = 1.0;

        if (equippedItem != null && equippedItem.getItemType().equals(ItemType.WEAPON)) {
            WeaponItem weaponItem = (WeaponItem) equippedItem;
            dmgMult = weaponItem.getDmgMultiplier();
            Main.LOGGER.info("Damage: " + dmgMult);
        }
        //seems to be fixed with rounding the HP value and not transforming it to the form X.X before
        double totalDamage = Constants.BASE_DAMAGE_MULTIPLIER * dmgMult;
        double previousHP = currentHP;

        currentHP -= totalDamage;
        currentHP = Main.generator.roundDouble(currentHP, 1);
        Main.LOGGER.info("Damage done to block: " + type + "." +
                " Damage: " + totalDamage + ", " + previousHP + "->" + currentHP);
    }

    /**
     * Creates a copy of the current block object
     *
     * @return A new instance of the block object
     */
    public Block copy() {
        return new Block(
                name,
                type,
                blockHP,
                imgFile,
                xpEnabled,
                spawnableMobSubTypes
        );
    }
}
