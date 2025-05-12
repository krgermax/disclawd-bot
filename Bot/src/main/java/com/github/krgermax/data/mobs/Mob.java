package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.DataObject;

import java.io.File;

public abstract class Mob extends DataObject {
    private final File imgFile;
    private final double spawnChance;
    private final MobType mobType;
    private final MobSubType mobSubType;

    public Mob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            File imgFile,
            double spawnChance
    ) {
        super(id, name, desc);
        this.imgFile = imgFile;
        this.spawnChance = spawnChance;
        this.mobType = mobType;
        this.mobSubType = mobSubType;
    }

    public File getImgFile() {
        return imgFile;
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
