package com.github.krgermax.data.inventory;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.UtilItem;
import com.github.krgermax.data.items.WeaponItem;
import com.github.krgermax.data.items.enums.ItemType;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private final String userID;
    private LocalDateTime timestamp;
    private List<EmbedBuilder> inventoryPages;

    public Inventory(String userID, SlashCommandInteractionEvent event) {
        this.userID = userID;
        this.timestamp = LocalDateTime.now();
        this.inventoryPages = new ArrayList<>();
        createPagesWrapper(event);
    }

    public Inventory(String userID, ButtonInteractionEvent event) {
        this.userID = userID;
        this.inventoryPages = new ArrayList<>();
        createPagesWrapper(event);
    }

    public void createPagesWrapper(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        String userName = event.getUser().getName();
        String userAvatarURL = event.getUser().getAvatarUrl();

        this.inventoryPages = createPages(userID, userName, userAvatarURL);
    }

    public void createPagesWrapper(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        String userName = event.getUser().getName();
        String userAvatarURL = event.getUser().getAvatarUrl();

        this.inventoryPages = createPages(userID, userName, userAvatarURL);
    }

    private List<EmbedBuilder> createPages(String userID, String userName, String userAvatarURL) {
        List<EmbedBuilder> embedBuilders = new ArrayList<>();
        List<Item> userItemList = Main.sqlHandler.sqlInventoryHandler.getAllUserItems(userID);
        int inventoryPagesCount = (int) Math.ceil((double) userItemList.size() / Constants.ITEMS_PER_PAGE);

        embedBuilders.add(createMainPage(userID, userName, userAvatarURL, (inventoryPagesCount + 1)));

        for (int i = 0; i < inventoryPagesCount; i++) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Inventory - " + userName);
            embedBuilder.setColor(Color.GREEN);
            embedBuilder.setFooter("Page: " + (i + 2) + "/" + (inventoryPagesCount + 1));

            int startIndex = i * Constants.ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + Constants.ITEMS_PER_PAGE, userItemList.size());

            for (int j = startIndex; j < endIndex; j++) {
                Item item = userItemList.get(j);

                String itemName = item.getName();
                double itemXPMult = item.getXpMultiplier();
                String alternativeTxt;
                double alternativePerk;

                if (item.getItemType() == ItemType.UTILITY) {
                    UtilItem utilItem = (UtilItem) item;
                    alternativeTxt = ":black_small_square: Gold boost: ";
                    alternativePerk = utilItem.getGoldMultiplier();
                } else {
                    WeaponItem weaponItem = (WeaponItem) item;
                    alternativeTxt = ":black_small_square: Damage boost: ";
                    alternativePerk = weaponItem.getDmgMultiplier();
                }

                embedBuilder.addField(
                        item.getEmoji() + itemName + item.getEmoji(),
                        ":black_small_square: XP boost: " + itemXPMult + "\n"
                                + alternativeTxt + alternativePerk,
                        true);
            }
            embedBuilders.add(embedBuilder);
        }
        return embedBuilders;
    }

    private EmbedBuilder createMainPage(String userID, String userName, String userAvatarURL, int inventoryPagesCount) {
        UserStats userStats = Main.sqlHandler.sqlStatsHandler.getUserStats(userID);
        int minedCount = userStats.getMinedCount();
        int mobKills = userStats.getMobKills();
        int bossKills = userStats.getBossKills();
        double xpCount = userStats.getXpCount();
        int goldCount = userStats.getGoldCount();

        double baseXP = Constants.BASE_XP_MULTIPLIER;
        double baseGold = Constants.BASE_GOLD_MULTIPLIER;
        double baseDMG = Constants.BASE_GOLD_MULTIPLIER;

        int equippedItemID = Main.sqlHandler.sqlInventoryHandler.getEquippedItemIDFromUser(userID);
        Item equippedItem = Main.shopHandler.getItemByID(equippedItemID);

        double calculatedXPMult = 1.0;
        double calculatedGoldMult = 1.0;
        double calculatedDmgMult = 1.0;

        String equippedItemName = "Nothing equipped";
        String itemEmoji = "";

        if (equippedItem != null) {
            calculatedXPMult = equippedItem.getXpMultiplier() * baseXP;
            equippedItemName = equippedItem.getName();
            itemEmoji = equippedItem.getEmoji();

            if (equippedItem.getItemType().equals(ItemType.UTILITY)) {
                UtilItem utilItem = (UtilItem) equippedItem;
                calculatedGoldMult = utilItem.getGoldMultiplier() * baseGold;

            } else if (equippedItem.getItemType().equals(ItemType.WEAPON)) {
                WeaponItem weaponItem = (WeaponItem) equippedItem;
                calculatedDmgMult = weaponItem.getDmgMultiplier() * baseDMG;
            }
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Inventory - " + userName);
        embedBuilder.setColor(Color.GREEN);
        embedBuilder.setThumbnail(userAvatarURL);

        embedBuilder.setDescription("Level " + Main.generator.computeLevel(xpCount));
        embedBuilder.addField(
                ":bar_chart:Stats",
                "> Times Mined: " + minedCount + " \n" +
                        "> Mob Kills: " + mobKills + " \n" +
                        "> Boss Kills: " + bossKills + " \n" +
                        "> XP: " + xpCount + " \n" +
                        "> Gold: " + goldCount,
                true);
        embedBuilder.addBlankField(true);
        embedBuilder.addField(
                itemEmoji +"Equipped Item",
                "> " + equippedItemName + " \n" +
                        ":dna:**Multipliers**" + " \n" +
                        "> XP: " + calculatedXPMult + " \n" +
                        "> Gold: " + calculatedGoldMult + " \n" +
                        "> Damage: " + calculatedDmgMult,
                true);
        embedBuilder.setFooter("Page: 1/" + inventoryPagesCount);
        return embedBuilder;
    }

    public String getUserID() {
        return userID;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public List<EmbedBuilder> getInventoryPages() {
        return this.inventoryPages;
    }
}
