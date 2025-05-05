package com.github.krgermax.data.items;

import com.github.krgermax.data.items.enums.ItemType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.github.krgermax.data.inventory.UserStats;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class WeaponItem extends Item {
    private final double dmgMultiplier;

    public WeaponItem(
            int uniqueID,
            String name,
            String emoji,
            String desc,
            String imgPath,
            int reqLvl,
            ItemType itemType,
            double dropChance,
            double xpMultiplier,
            double dmgMultiplier
    ) {
        super(uniqueID, name, desc, emoji, imgPath, reqLvl, itemType, dropChance, xpMultiplier);
        this.dmgMultiplier = dmgMultiplier;
        this.price = calculatePrice(xpMultiplier, dmgMultiplier);
    }

    @Override
    public Field createShopField() {
        return new Field(
                this.getEmoji() + this.getName() + this.getEmoji(),
                Constants.BLACK_SMALL_SQUARE + " XP boost: " + this.getXpMultiplier() + "\n"
                        + Constants.BLACK_SMALL_SQUARE + " Damage boost: " + this.getDmgMultiplier() + "\n"
                        + Constants.BLACK_SMALL_SQUARE + " Price: " + this.getPrice() + " Coins" + "\n"
                        + Constants.BLACK_SMALL_SQUARE + " lvl. " + this.getReqLvl(),
                true
        );
    }

    @Override
    public EmbedBuilder createInspectEmbed(UserStats userStats, Button buyButton) {
        String priceEmoji = Constants.BLACK_SMALL_SQUARE;
        String lvlEmoji = Constants.BLACK_SMALL_SQUARE;
        if (buyButton.isDisabled()) {
            priceEmoji = userStats.getGoldCount() < this.getPrice() ? Constants.RED_CROSS : Constants.BLACK_SMALL_SQUARE;
            lvlEmoji = Main.generator.computeLevel(userStats.getXpCount()) < this.getReqLvl() ? Constants.RED_CROSS : Constants.BLACK_SMALL_SQUARE;
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(":mag: " + this.getName() + " :mag:")
                .setThumbnail("attachment://item.png")
                .addField(
                        this.getDescription(),
                        Constants.BLACK_SMALL_SQUARE + " XP boost: " + this.getXpMultiplier() + "\n"
                                + Constants.BLACK_SMALL_SQUARE + " Damage boost: " + this.getDmgMultiplier() + "\n"
                                + priceEmoji + " Price: " + this.getPrice() + " Coins" + "\n"
                                + lvlEmoji + " Required lvl. " + this.getReqLvl(),
                        false
                );
        return embedBuilder;
    }

    public double getDmgMultiplier() {
        return dmgMultiplier;
    }
}
