package com.github.krgermax.buttons.type;

import com.github.krgermax.buttons.ButtonManager;
import com.github.krgermax.buttons.CustomButton;
import com.github.krgermax.data.mobs.NormalMob;
import com.github.krgermax.main.Main;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;


public class HitButton implements CustomButton {
    @Override
    public void executeButton(ButtonInteractionEvent event) {
        String userID = event.getUser().getId();
        if (!Main.sqlHandler.isUserRegistered(userID)) {
            Main.sqlHandler.registerUser(userID);
            Main.sqlHandler.sqlEmbeddedHandler.replyToNewRegisteredUser(event);
        } else {
            String componentId = event.getComponentId();
            int mobID = Integer.parseInt(componentId.replace(ButtonManager.HIT_BUTTON_ID, ""));

            NormalMob spawnedMob = (NormalMob) Main.mineworldManager.getMobByID(mobID);
            if (spawnedMob == null)
                return;

            /*
                Defer the interaction immediately to acknowledge it within Discord's 3-second timeout window.
                Execute the rest of the logic only after the message is successfully deleted.

                Placing the XP and gold update logic inside the success callback ensures that:
                - A user cannot receive rewards multiple times from the same mob.
                - If the message deletion fails (e.g., due to the button being used more than once),
                  no stats are updated and the interaction is safely aborted.
            */
            event.deferEdit().queue();
            event.getMessage().delete().queue(
                    success -> {
                        double userCurrentXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);
                        Main.sqlHandler.sqlStatsHandler.updateUserStatsAfterKill(userID, spawnedMob.getGoldDrop(), spawnedMob.getXpDrop());
                        double userUpdatedXP = Main.sqlHandler.sqlStatsHandler.getXPCountFromUser(userID);

                        Main.sqlHandler.sqlStatsHandler.replyToUserLevelUp(userCurrentXP, userUpdatedXP, event);
                        Main.LOGGER.info("Executed '" + ButtonManager.HIT_BUTTON_ID + "' button");
                    },
                    failure -> Main.LOGGER.severe("Did not execute '" + ButtonManager.HIT_BUTTON_ID + "' button. Failed to delete message: " + failure.getMessage())
            );
        }
    }
}
