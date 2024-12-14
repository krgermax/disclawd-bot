package org.clawd.data.mobs;

import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.data.mobs.enums.MobType;

public class NormalMob extends Mob {
    private final double xpDrop;
    private final int goldDrop;

    public NormalMob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance,
            double xpDrop,
            int goldDrop
    ) {
        super(id, name, desc, mobType, mobSubType, imgPath, spawnChance);
        this.xpDrop = xpDrop;
        this.goldDrop = goldDrop;
    }

    public double getXpDrop() {
        return xpDrop;
    }

    public int getGoldDrop() {
        return goldDrop;
    }
}
