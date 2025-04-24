package org.clawd.buttons.type;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.buttons.CustomButton;
import org.clawd.data.items.Item;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

public class MineButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();

        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            int itemID = Main.sqlHandler.sqlInventoryHandler.getEquippedItemIDFromUser(userID);
            Item equippedItem = Main.shopHandler.getItemByID(itemID);
            double generatedXP = Main.generator.generateXP();
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

            Main.mineworld.updateCurrentUserMultiplication(userID);
            Main.mineworld.updateBiome(event, equippedItem);

            double userCurrentXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);
            Main.sqlHandler.sqlStatsHandler.updateUserStatsAfterMining(userID, combinedXP, combinedGold);
            double userUpdatedXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);

            Main.sqlHandler.sqlStatsHandler.replyToUserLevelUp(userCurrentXP, userUpdatedXP, event);
            Main.mobSpawner.spawnMob(event.getChannel());

            Main.LOGGER.info("Executed '" + Constants.MINE_BUTTON_ID + "' button");
        }
    }

    private double getItemGoldMult(Item equippedItem) {
        if (equippedItem.getItemType() == ItemType.UTILITY)
            return ((UtilItem) equippedItem).getGoldMultiplier();
        return Constants.BASE_GOLD_MULTIPLIER;
    }
}
