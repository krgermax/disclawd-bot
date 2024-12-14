package org.clawd.commands.type.slashcommand;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.clawd.data.inventory.UserStats;
import org.clawd.data.items.Item;
import org.clawd.data.items.UtilItem;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.io.File;
import java.util.Objects;

public class ItemCommand implements SlashCommand{
    @Override
    public void executeCommand(SlashCommandInteractionEvent event) {
        String searchTerm = getSearchTerm(event);

        Item foundItem = Main.mineworld.getItemByName(searchTerm);
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);

        if (foundItem == null) {
            embedBuilder.setDescription("Item **" + searchTerm + "** was not found, maybe you mistyped something :/");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        } else {
            UserStats userStats = fetchUserStats(event);
                /*
                 * TODO: a lot of this can be done in the respective item class, pass user stats to method to dynamically
                 *  change the values of the embedded message
                 */

            int userLvl = Main.generator.computeLevel(userStats.getXpCount());
            int userGold = userStats.getGoldCount();

            String priceCheck = ":black_small_square:";
            String lvlCheck = ":black_small_square:";

            Button buyButton = createBuyButton(event, foundItem, userLvl, userGold);
            Button equipButton = createEquipButton(event, foundItem, event.getUser().getId());

            if (buyButton.isDisabled()) {
                priceCheck = userGold < foundItem.getPrice() ? ":x:" : priceCheck;
                lvlCheck = userLvl < foundItem.getReqLvl() ? ":x:" : lvlCheck;
            }

            double alternativePerk = extractAlternativePerk(foundItem);
            String alternativeTxt = generateAlternativeText(foundItem);

            populateEmbed(embedBuilder, foundItem, alternativeTxt, alternativePerk, priceCheck, lvlCheck);
            sendResponse(event, embedBuilder, foundItem, buyButton, equipButton);
        }
    }

    private String getSearchTerm(SlashCommandInteractionEvent event) {
        return Objects.requireNonNull(event.getOption(Constants.ITEM_COMMAND_OPTION_ID)).getAsString();
    }

    private UserStats fetchUserStats(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        return Main.sqlHandler.sqlStatsHandler.getUserStats(userID);
    }

    private Button createBuyButton(SlashCommandInteractionEvent event, Item foundItem, int userLvl, int userGold) {
        Button buyButton = Button.success(Constants.BUY_BUTTON_ID + foundItem.getID(), "Buy");
        boolean isItemInUserInv = Main.sqlHandler.sqlInventoryHandler.isItemInUserInventory(event.getUser().getId(), foundItem.getID());
        if (isItemInUserInv || userLvl < foundItem.getReqLvl() || userGold < foundItem.getPrice()) {
            buyButton = buyButton.asDisabled();
        }
        return buyButton;
    }

    private Button createEquipButton(SlashCommandInteractionEvent event, Item foundItem, String userID) {
        Button equipButton = Button.success(Constants.EQUIP_BUTTON_ID + foundItem.getID(), "Equip");
        boolean isItemInUserInv = Main.sqlHandler.sqlInventoryHandler.isItemInUserInventory(event.getUser().getId(), foundItem.getID());
        if (!isItemInUserInv) {
            equipButton = equipButton.asDisabled();
        }
        return equipButton;
    }

    private double extractAlternativePerk(Item foundItem) {
        if (foundItem.getItemType() == ItemType.UTILITY) {
            return ((UtilItem) foundItem).getGoldMultiplier();
        } else {
            return ((WeaponItem) foundItem).getDmgMultiplier();
        }
    }

    private String generateAlternativeText(Item foundItem) {
        return foundItem.getItemType() == ItemType.UTILITY
                ? ":black_small_square: Gold boost: "
                : ":black_small_square: Damage boost: ";
    }

    private void populateEmbed(EmbedBuilder embedBuilder, Item foundItem, String alternativeTxt, double alternativePerk, String priceCheck, String lvlCheck) {
        embedBuilder.setTitle(":mag: " + foundItem.getName() + " :mag:")
                .setThumbnail("attachment://item.png")
                .addField(
                        foundItem.getDescription(),
                        ":black_small_square: XP boost: " + foundItem.getXpMultiplier() + "\n" +
                                alternativeTxt + alternativePerk + "\n" +
                                priceCheck + " Price: " + foundItem.getPrice() + " Coins" + "\n" +
                                lvlCheck + " Required lvl. " + foundItem.getReqLvl(),
                        false
                );
    }

    private void sendResponse(SlashCommandInteractionEvent event, EmbedBuilder embedBuilder, Item foundItem, Button buyButton, Button equipButton) {
        File imgFile = new File(foundItem.getImgPath());
        event.replyEmbeds(embedBuilder.build())
                .addFiles(FileUpload.fromData(imgFile, "item.png"))
                .addActionRow(buyButton, equipButton)
                .setEphemeral(true)
                .queue();

        Main.LOG.info("Executed '" + Constants.ITEM_COMMAND_ID + "' command");
    }
}
