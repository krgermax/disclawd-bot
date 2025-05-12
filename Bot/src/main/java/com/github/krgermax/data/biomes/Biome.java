package com.github.krgermax.data.biomes;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.WeaponItem;
import com.github.krgermax.data.items.enums.ItemType;
import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.io.File;
import java.util.Set;

public class Biome {
    private final String name;
    private final BiomeType type;
    private final double biomeHP;
    private double currentHP;
    private double adjustableFullHP;
    private final File imgFile;
    private final boolean xpEnabled;
    private final Set<MobSubType> spawnableMobSubTypes;

    public Biome(String name, BiomeType type, double biomeHP, File imgFile, boolean xpEnabled, Set<MobSubType> spawnableMobSubTypes) {
        this.name = name;
        this.type = type;
        this.biomeHP = biomeHP;

        this.currentHP = biomeHP;
        this.adjustableFullHP = biomeHP;

        this.imgFile = imgFile;

        this.xpEnabled = xpEnabled;
        this.spawnableMobSubTypes = spawnableMobSubTypes;
    }

    public String getName() {
        return name;
    }

    public BiomeType getType() {
        return type;
    }

    public double getTrueHP() {
        return biomeHP;
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
     * Calculates the damage done to a biome by a user and applies it to
     * the current HP of the current biome
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
        Main.LOGGER.info("Damage done to biome: " + type + "." +
                " Damage: " + totalDamage + ", " + previousHP + "->" + currentHP);
    }

    /**
     * Creates a copy of the current biome object
     *
     * @return A new instance of the Biome object
     */
    public Biome copy() {
        return new Biome(
                name,
                type,
                biomeHP,
                imgFile,
                xpEnabled,
                spawnableMobSubTypes
        );
    }
}
