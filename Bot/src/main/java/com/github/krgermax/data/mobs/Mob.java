package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.DataObject;

public abstract class Mob extends DataObject {
    private final String imgPath;
    private final double spawnChance;
    private final MobType mobType;
    private final MobSubType mobSubType;

    public Mob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance
    ) {
        super(id, name, desc);
        this.mobType = mobType;
        this.mobSubType = mobSubType;
        this.imgPath = imgPath;
        this.spawnChance = spawnChance;
    }

    public String getImgPath() {
        return imgPath;
    }

    public double getSpawnChance() {
        return spawnChance;
    }

    public MobType getMobType() {
        return mobType;
    }

    public MobSubType getMobSubType() {
        return mobSubType;
    }
}
