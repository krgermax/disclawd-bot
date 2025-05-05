package com.github.krgermax.buttons.type;

import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.items.UtilItem;
import com.github.krgermax.data.items.enums.ItemType;
import com.github.krgermax.data.mineworld.Mineworld;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class MineButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        String guildID = event.getGuild().getId();
        Mineworld mineworld = Main.mineworldManager.getMineworld(guildID);

        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            int itemID = Main.sqlHandler.sqlInventoryHandler.getEquippedItemIDFromUser(userID);
            Item equippedItem = Main.shopManager.getItemByID(itemID);

            double generatedXP = Main.generator.generateXP(mineworld);
            int generatedGold = Main.generator.generateGold();

            double itemXPMult = Constants.BASE_XP_MULTIPLIER;
            double itemGoldMult = Constants.BASE_GOLD_MULTIPLIER;

            if (equippedItem != null) {
                itemXPMult = equippedItem.getXpMultiplier();
                itemGoldMult = getItemGoldMult(equippedItem);
            }

            double combinedXP = Main.generator.roundDouble((generatedXP * itemXPMult), 2);
            int combinedGold = (int) Math.ceil(generatedGold * itemGoldMult);

            Main.LOGGER.info("XP after multiplier : " + combinedXP);
            Main.LOGGER.info("Gold after multiplier: " + combinedGold);

            mineworld.updateCurrentUserMultiplication(userID);
            mineworld.updateBiome(event, equippedItem);

            double userCurrentXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);
            Main.sqlHandler.sqlStatsHandler.updateUserStatsAfterMining(userID, combinedXP, combinedGold);
            double userUpdatedXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);

            Main.sqlHandler.sqlStatsHandler.replyToUserLevelUp(userCurrentXP, userUpdatedXP, event);

            Main.LOGGER.info("Executed '" + Constants.MINE_BUTTON_ID + "' button");
        }
    }

    private double getItemGoldMult(Item equippedItem) {
        if (equippedItem.getItemType() == ItemType.UTILITY)
            return ((UtilItem) equippedItem).getGoldMultiplier();
        return Constants.BASE_GOLD_MULTIPLIER;
    }
}
