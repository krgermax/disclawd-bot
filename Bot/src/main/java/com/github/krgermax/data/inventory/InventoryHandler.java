package com.github.krgermax.data.inventory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import com.github.krgermax.tokens.Constants;


import java.util.Objects;

public class InventoryHandler {

    public final InventoryCache inventoryCache = new InventoryCache();
    private Button nextButton = Button.secondary(Constants.NEXT_INV_BUTTON_ID, Constants.NEXT_BUTTON_EMOJI);
    private final Button homeButton = Button.secondary(Constants.HOME_INV_BUTTON_ID, Constants.HOME_BUTTON_EMOJI);
    private Button backButton = Button.secondary(Constants.BACK_INV_BUTTON_ID, Constants.BACK_BUTTON_EMOJI);

    /**
     * Replies with the first page of the inventory
     *
     * @param event The SlashCommandInteractionEvent event
     */
    public void replyWithInventoryFirstEmbedded(SlashCommandInteractionEvent event) {
        Inventory inventory = this.inventoryCache.addInventory(event);
        if (inventory.getInventoryPages().size() < 2) {
            this.nextButton = this.nextButton.asDisabled();
        } else {
            this.nextButton = this.nextButton.asEnabled();
        }
        event.replyEmbeds(inventory.getInventoryPages().getFirst().build())
                .addActionRow(
                        this.backButton.asDisabled(),
                        this.homeButton,
                        this.nextButton
                )
                .setEphemeral(true)
                .queue();
    }

    /**
     * Replies if either the next page or back page button was pressed by a user. Retrieves the current page from the
     * page footer. Then calculates the next page and selects it. Generates correct buttons in each case and updates message
     * with new embed and buttons.
     *
     * @param event The ButtonInteractionEvent event
     * @param back  True if back button else false
     */
    public void replyToNextInvPage(ButtonInteractionEvent event, boolean back) {
        Inventory inventory = this.inventoryCache.addInventory(event);
        String footer = Objects.requireNonNull(event.getMessage().getEmbeds().getFirst().getFooter()).getText();
        String[] parts = footer.split("/");
        int currentPage = Integer.parseInt(parts[0].substring(6).strip());

        if (back) {
            currentPage--;
        } else {
            currentPage++;
        }

        currentPage = Math.max(1, Math.min(currentPage, inventory.getInventoryPages().size())) - 1;

        EmbedBuilder embedBuilder = inventory.getInventoryPages().get(currentPage);
        this.backButton = this.backButton.asEnabled();
        this.nextButton = this.nextButton.asEnabled();

        if (currentPage == 0) {
            this.backButton = this.backButton.asDisabled();
        } else if (currentPage == inventory.getInventoryPages().size() - 1) {
            this.nextButton = this.nextButton.asDisabled();
        }

        InteractionHook hook = event.editMessageEmbeds(embedBuilder.build()).complete();
        hook.editOriginalComponents(ActionRow.of(this.backButton, this.homeButton, this.nextButton)).queue();
    }

    /**
     * This method makes the embedded message jump back to the first page and
     * forces an update on the users inventory
     *
     * @param event The ButtonInteractionEvent event
     */
    public void updateToFirstEmbedded(ButtonInteractionEvent event) {
        Inventory inventory = this.inventoryCache.forceInventoryUpdate(event);
        this.nextButton = this.nextButton.asEnabled();
        this.backButton = this.backButton.asDisabled();

        if (inventory.getInventoryPages().size() < 2)
            this.nextButton = this.nextButton.asDisabled();

        InteractionHook hook = event.editMessageEmbeds(inventory.getInventoryPages().getFirst().build()).complete();
        hook.editOriginalComponents(ActionRow.of(this.backButton, this.homeButton, this.nextButton)).queue();
    }
}
