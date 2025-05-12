package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.mobs.enums.MobSubType;

import java.io.File;

public class BossMob extends NormalMob {
    private final boolean specialDrop;
    private final double health;

    public BossMob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            File imgFile,
            double spawnChance,
            double xpDrop,
            int goldDrop,
            boolean specialDrop,
            double health
    ) {
        super(id, name, desc, mobType, mobSubType, imgFile, spawnChance, xpDrop, goldDrop);
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
