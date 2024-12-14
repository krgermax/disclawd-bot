package org.clawd.data.mobs;

import org.clawd.data.DataObject;
import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.data.mobs.enums.MobType;

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
