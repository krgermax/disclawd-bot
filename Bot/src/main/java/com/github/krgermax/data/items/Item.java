package com.github.krgermax.data.items;

import com.github.krgermax.data.items.enums.ItemType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.github.krgermax.data.DataObject;
import com.github.krgermax.data.inventory.UserStats;

public abstract class Item extends DataObject {

    private final String emoji;
    private final String imgPath;
    protected int price;
    private final double dropChance;
    private final double xpMultiplier;
    protected final int reqLvl;
    private final ItemType itemType;

    public Item(
            int uniqueID,
            String name,
            String desc,
            String emoji,
            String imgPath,
            int reqLvl,
            ItemType itemType,
            double dropChance,
            double xpMultiplier
    ) {
        super(uniqueID, name, desc);
        this.emoji = emoji;
        this.imgPath = imgPath;
        this.price = 0;
        this.dropChance = dropChance;
        this.xpMultiplier = xpMultiplier;
        this.reqLvl = reqLvl;
        this.itemType = itemType;
    }

    public int getPrice() {
        return price;
    }

    /**
     * @param perkOne The first perk of an item
     * @param perkTwo The second perk of an item
     * @return The price following a formula:
     * - ((perkOne * 10) mod 10) * 300 + ((perkTwo * 10) mod 10) * 300 + (lvlReq/0.2)^2
     */
    protected int calculatePrice(double perkOne, double perkTwo) {
        double xpMultDif = (perkOne * 10) % 10;
        double goldMultDif = (perkTwo * 10) % 10;

        int firstPerkGoldIncrease = (int) xpMultDif * 300;
        int scdPerkGoldIncrease = (int) goldMultDif * 300;

        int additionalIncrease = 0;
        if (firstPerkGoldIncrease > 0 && scdPerkGoldIncrease > 0)
            additionalIncrease = 200;

        int formulaResult = (int) ((this.reqLvl / 0.2) * (this.reqLvl / 0.2));

        return firstPerkGoldIncrease + scdPerkGoldIncrease + additionalIncrease + formulaResult;
    }

    public abstract Field createShopField();

    public abstract EmbedBuilder createInspectEmbed(UserStats userStats, Button buyButton, Button equipButton);

    public double getDropChance() {
        return dropChance;
    }

    public double getXpMultiplier() {
        return xpMultiplier;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public String getEmoji() {
        return emoji;
    }

    public int getReqLvl() {
        return reqLvl;
    }

    public String getImgPath() {
        return imgPath;
    }
}
