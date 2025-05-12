package com.github.krgermax.data.mobs;

import com.github.krgermax.data.mobs.enums.MobType;
import com.github.krgermax.data.mobs.enums.TradeType;
import com.github.krgermax.data.mobs.enums.MobSubType;

import java.io.File;

public class TradeMob extends Mob {
    private final TradeType tradeType;
    public TradeMob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            File imgFile,
            double spawnChance,
            TradeType tradeType
    ) {
        super(id, name, desc, mobType, mobSubType, imgFile, spawnChance);
        this.tradeType = tradeType;
    }

    public TradeType getTradeType() {
        return tradeType;
    }
}
