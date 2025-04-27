package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.mobs.enums.MobSubType;

public class BossMob extends NormalMob {
    private final boolean specialDrop;
    private final double health;

    public BossMob(
            int id,
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
        super(id, name, desc, mobType, mobSubType, imgPath, spawnChance, xpDrop, goldDrop);
        this.specialDrop = specialDrop;
        this.health = health;
    }

    public boolean getSpecialDrop() {
        return specialDrop;
    }

    public double getHealth() {
        return health;
    }
}
