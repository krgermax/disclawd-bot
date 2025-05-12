package com.github.krgermax.data.items;

import com.github.krgermax.data.items.enums.ItemType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.github.krgermax.data.inventory.UserStats;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.io.File;

public class UtilItem extends Item {
    private final double goldMultiplier;

    public UtilItem(
            int uniqueID,
            String name,
            String desc,
            String itemEmoji,
            File imgFile,
            int reqLvl,
            ItemType itemType,
            double dropChance,
            double xpMultiplier,
            double goldMultiplier
    ) {
        super(uniqueID, name, desc, itemEmoji, imgFile, reqLvl, itemType, dropChance, xpMultiplier);
        this.goldMultiplier = goldMultiplier;
        this.price = calculatePrice(xpMultiplier, goldMultiplier);
    }

    @Override
    public Field createShopField() {
        return new Field(
                this.getEmoji() + this.getName() + this.getEmoji(),
                Constants.BLACK_SMALL_SQUARE + " XP boost: " + this.getXpMultiplier() + "\n"
                        + Constants.BLACK_SMALL_SQUARE + " Gold boost: " + this.getGoldMultiplier() + "\n"
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
                                + Constants.BLACK_SMALL_SQUARE + " Gold boost: " + this.getGoldMultiplier() + "\n"
                                + priceEmoji + " Price: " + this.getPrice() + " Coins" + "\n"
                                + lvlEmoji + " Required lvl. " + this.getReqLvl(),
                        false
                );

        return embedBuilder;
    }

    public double getGoldMultiplier() {
        return goldMultiplier;
    }
}
