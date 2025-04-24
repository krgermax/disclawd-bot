package org.clawd.data.biomes;

import org.clawd.data.items.Item;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.util.Set;

import java.util.Set;

public class Biome {
    private final String name;
    private final BiomeType type;
    private final double biomeHP;
    private double currentHP;
    private double adjustableFullHP;
    private final String imgPath;
    private final boolean xpEnabled;
    private final Set<MobSubType> spawnableMobSubTypes;

    public Biome(String name, BiomeType type, double biomeHP, String imgPath, boolean xpEnabled, Set<MobSubType> spawnableMobSubTypes) {
        this.name = name;
        this.type = type;
        this.biomeHP = biomeHP;

        this.currentHP = biomeHP;
        this.adjustableFullHP = biomeHP;

        this.imgPath = imgPath;
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

    public String getImgPath() {
        return imgPath;
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
            Main.LOGGER.info(String.valueOf(dmgMult));
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
     * Instead of destroying a biome object, one can choose to reset its HP
     */
    public void reset() {
        currentHP = biomeHP;
        adjustableFullHP = biomeHP;
    }
}
