package org.clawd.data.mobs;

import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.data.mobs.enums.MobType;
import org.clawd.data.mobs.enums.TradeType;

public class TradeMob extends Mob {
    private final TradeType tradeType;
    public TradeMob(
            int id,
            String name,
            String desc,
            MobType mobType,
            MobSubType mobSubType,
            String imgPath,
            double spawnChance,
            TradeType tradeType
    ) {
        super(id, name, desc, mobType, mobSubType, imgPath, spawnChance);
        this.tradeType = tradeType;
    }

    public TradeType getTradeType() {
        return tradeType;
    }
}
