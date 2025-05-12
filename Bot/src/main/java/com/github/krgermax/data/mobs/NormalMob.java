package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.data.mobs.enums.MobType;

import java.io.File;

public class NormalMob extends Mob {
    private final double xpDrop;
    private final int goldDrop;

    public NormalMob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            File imgFile,
            double spawnChance,
            double xpDrop,
            int goldDrop
    ) {
        super(id, name, desc, mobType, mobSubType, imgFile, spawnChance);
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
