package org.clawd.buttons.type;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.clawd.buttons.CustomButton;
import org.clawd.data.mobs.NormalMob;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

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

            NormalMob spawnedMob = (NormalMob) Main.mineworld.getMobByID(mobID);
            if (spawnedMob == null)
                return;

            event.getMessage().delete().queue();
            event.deferEdit().queue();

            Main.sqlHandler.sqlStatsHandler.updateUserStatsAfterKill(userID, spawnedMob.getGoldDrop(), spawnedMob.getXpDrop());

            Main.LOG.info("Executed '" + Constants.HIT_BUTTON_ID + "' button");
        }
    }
}
