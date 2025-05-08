package com.github.krgermax.buttons;

import com.github.krgermax.buttons.type.BuyButton;
import com.github.krgermax.buttons.type.EquipButton;
import com.github.krgermax.buttons.type.HitButton;
import com.github.krgermax.buttons.type.MineButton;
import com.github.krgermax.buttons.type.nav.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import com.github.krgermax.main.Main;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ButtonManager extends ListenerAdapter {

    private final HashMap<String, CustomButton> buttons;
    private final HashMap<String, String> buttonsWithID;

    /*
     * Button Id's
     */
    public static final String MINE_BUTTON_ID = "mine";
    public static final String NEXT_SHOP_BUTTON_ID = "next-shop";
    public static final String BACK_SHOP_BUTTON_ID = "back-shop";
    public static final String HOME_SHOP_BUTTON_ID = "home-shop";
    public static final String NEXT_INV_BUTTON_ID = "next-inv";
    public static final String HOME_INV_BUTTON_ID = "home-inv";
    public static final String BACK_INV_BUTTON_ID = "back-inv";
    public static final String BUY_BUTTON_ID = "buy-";
    public static final String EQUIP_BUTTON_ID = "equip-";
    public static final String HIT_BUTTON_ID = "hit-mob-";

    public ButtonManager() {
        this.buttons = new HashMap<>();

        this.buttons.put(MINE_BUTTON_ID, new MineButton());
        this.buttons.put(NEXT_SHOP_BUTTON_ID, new ShopNextButton());
        this.buttons.put(BACK_SHOP_BUTTON_ID, new ShopBackButton());
        this.buttons.put(HOME_SHOP_BUTTON_ID, new ShopHomeButton());
        this.buttons.put(BUY_BUTTON_ID, new BuyButton());
        this.buttons.put(EQUIP_BUTTON_ID, new EquipButton());
        this.buttons.put(NEXT_INV_BUTTON_ID, new InvNextButton());
        this.buttons.put(BACK_INV_BUTTON_ID, new InvBackButton());
        this.buttons.put(HOME_INV_BUTTON_ID, new InvHomeButton());
        this.buttons.put(HIT_BUTTON_ID, new HitButton());

        this.buttonsWithID = new HashMap<>();

        this.buttonsWithID.put(HIT_BUTTON_ID, HIT_BUTTON_ID);
        this.buttonsWithID.put(EQUIP_BUTTON_ID, EQUIP_BUTTON_ID);
        this.buttonsWithID.put(BUY_BUTTON_ID, BUY_BUTTON_ID);
    }

    /**
     * Receives a button interaction event and calls on the corresponding
     * button class executeButton() method
     *
     * @param event Received event
     */
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String buttonID = event.getComponentId();
        Main.LOGGER.info("Received button interaction: " + buttonID);

        String key = buttonsWithID.entrySet().stream()
                .filter(entry -> buttonID.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(buttonID);

        CustomButton button = buttons.get(key);

        if (button != null) {
            button.executeButton(event);
        } else {
            Main.LOGGER.severe("Button ID not found: " + buttonID);
        }
    }
}
