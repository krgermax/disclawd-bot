package com.github.krgermax.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface CustomButton {

    /**
     * Function gets overridden by concrete button
     *
     * @param event The event to be handled
     */
    void executeButton(ButtonInteractionEvent event);
}
