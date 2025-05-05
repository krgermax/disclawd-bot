package com.github.krgermax.buttons.type;

import com.github.krgermax.data.mobs.NormalMob;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

public class HitButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            String componentId = event.getComponentId();
            int mobID = Integer.parseInt(componentId.replace(Constants.HIT_BUTTON_ID, ""));

            NormalMob spawnedMob = (NormalMob) Main.mineworldManager.getMobByID(mobID);
            if (spawnedMob == null)
                return;

            event.getMessage().delete().queue();
            event.deferEdit().queue();

            double userCurrentXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);
            Main.sqlHandler.sqlStatsHandler.updateUserStatsAfterKill(userID, spawnedMob.getGoldDrop(), spawnedMob.getXpDrop());
            double userUpdatedXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);

            Main.sqlHandler.sqlStatsHandler.replyToUserLevelUp(userCurrentXP, userUpdatedXP, event);

            Main.LOGGER.info("Executed '" + Constants.HIT_BUTTON_ID + "' button");
        }
    }
}
