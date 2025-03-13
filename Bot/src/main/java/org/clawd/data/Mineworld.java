package org.clawd.data;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;
import org.clawd.data.biomes.Biome;
import org.clawd.data.inventory.InventoryHandler;
import org.clawd.data.items.Item;
import org.clawd.data.items.WeaponItem;
import org.clawd.data.items.enums.ItemType;
import org.clawd.data.mobs.Mob;
import org.clawd.data.shop.ShopHandler;
import org.clawd.main.Main;
import org.clawd.tokens.Constants;

import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class Mineworld {
    public final ShopHandler shopHandler;
    public final InventoryHandler inventoryHandler;
    private final List<Item> itemList;
    private final List<Mob> mobList;
    private final List<Biome> biomeList;
    private Biome currentBiome;
    private Double currentBiomeHP;
    private Double currentBiomeFullHP;
    private final Map<String, LocalDateTime> currentUserMap;
    private int currentUserMultiplier;

    public Mineworld(List<Item> itemList, List<Mob> mobList, List<Biome> biomeList) {
        this.itemList = itemList;
        this.mobList = mobList;
        this.biomeList = biomeList;

        this.shopHandler = new ShopHandler(itemList);
        this.inventoryHandler = new InventoryHandler();
        this.currentUserMap = new HashMap<>();

        this.currentBiome = generateBiome();
        this.currentBiomeFullHP = currentBiome.biomeHP();
        this.currentBiomeHP = this.currentBiomeFullHP;

        this.currentUserMultiplier = 1;
    }

    /**
     * Selects a random biom from the biomes enum
     *
     * @return A biom
     */
    private Biome generateBiome() {
        int size = biomeList.size();
        int selector = (int) (Math.random() * size);

        Biome returnBiome = biomeList.get(selector);
        Main.LOG.info("The current biome is: " + returnBiome.type());

        return returnBiome;
    }

    private EmbedBuilder buildBiomeEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(currentBiome.name())
                .setColor(Color.BLACK)
                .setDescription("Active miners: " + currentUserMap.size() + " (Last " + Constants.MAX_MINE_NOT_INTERACTED_MINUTES + " minutes)")
                .addField("Biome HP", currentBiomeHP + "/" + currentBiomeFullHP, false)
                .setImage("attachment://ore.png");
        return embedBuilder;
    }

    private File getBiomeImageFile() {
        File imgFile = new File(currentBiome.imgPath());
        if (!imgFile.exists()) {
            Main.LOG.severe("Biome image not found: " + currentBiome.imgPath());
            return null;
        }
        return imgFile;
    }

    /**
     * This method replies to the '/biome' command, by building an embedded message
     * with all necessary information and buttons
     *
     * @param event Event
     */
    public void replyWithBiomeEmbedded(SlashCommandInteractionEvent event) {
        File imgFile = getBiomeImageFile();
        if (imgFile == null) return;

        event.replyEmbeds(buildBiomeEmbed().build())
                .addFiles(FileUpload.fromData(imgFile, "ore.png"))
                .addActionRow(Button.primary(Constants.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                .queue();
    }

    /**
     * This method replies to the 'mine' button, by building an embedded message
     * with all necessary information and buttons. This method overloads the method
     * with the SlashCommandInteractionEvent event param
     *
     * @param event Event
     */
    public void replyWithBiomeEmbedded(ButtonInteractionEvent event) {
        File imgFile = getBiomeImageFile();
        if (imgFile == null) return;

        event.replyEmbeds(buildBiomeEmbed().build())
                .addFiles(FileUpload.fromData(imgFile, "ore.png"))
                .addActionRow(Button.primary(Constants.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                .queue();
    }

    /**
     * Updates the embedded message on a 'ButtonInteractionEvent'
     *
     * @param event Event
     */
    private void updateBiomeMsg(ButtonInteractionEvent event) {
        File imgFile = getBiomeImageFile();
        if (imgFile == null) return;

        event.editMessageEmbeds(buildBiomeEmbed().build())
                .setFiles(FileUpload.fromData(imgFile, "ore.png"))
                .setActionRow(Button.primary(Constants.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                .queue();
        Main.LOG.info("Updated biome state.");
    }

    /**
     * Wrapper method to update the biome on a 'ButtonInteractionEvent'
     * and the embedded message, such that the current state is displayed
     * correctly
     *
     * @param event  Event
     * @param equippedItem The equipped user item
     */
    public void updateBiome(ButtonInteractionEvent event, Item equippedItem) {
        damageBiome(equippedItem);
        if (this.currentBiomeHP <= 0) {

            this.currentBiome = generateBiome();
            updateBiomeOnCompletion(event);
            /*
             * We need to update the mob spawner if a biome is completed and only if it is completed
             * to update the spawnable mob list, avoids unnecessary list filtering calls
             */
            Main.mobSpawner.updateSpawner();
            return;
        }
        updateBiomeMsg(event);
    }

    /**
     * Updates the biome state and embedded message on a 'ButtonInteractionEvent', if
     * the biome has been completed by reaching HP of <= 0
     *
     * @param event Event
     */
    private void updateBiomeOnCompletion(ButtonInteractionEvent event) {
        event.getMessage().delete().queue();
        this.currentBiomeFullHP = currentBiome.biomeHP();
        adjustCurrentBiomeHP();
        this.currentBiomeHP = currentBiomeFullHP;
        replyWithBiomeEmbedded(event);
        Main.LOG.info("Updated biome because of completion.");
    }

    /**
     * Calculates the damage done to a biome by a user and applies it to
     * the current HP of the current biome
     *
     * @param equippedItem The equipped user item
     */
    private void damageBiome(Item equippedItem) {
        double dmgMult = 1.0;

        if (equippedItem != null && equippedItem.getItemType().equals(ItemType.WEAPON)) {
            WeaponItem weaponItem = (WeaponItem) equippedItem;
            dmgMult = weaponItem.getDmgMultiplier();
            Main.LOG.info(String.valueOf(dmgMult));
        }
        //seems to be fixed with rounding the HP value and not transforming it to the form X.X before
        double totalDamage = Constants.BASE_DAMAGE_MULTIPLIER * dmgMult;
        double previousHP = this.currentBiomeHP;

        this.currentBiomeHP -= totalDamage;
        this.currentBiomeHP = Main.generator.roundDouble(this.currentBiomeHP, 1);
        Main.LOG.info("Damage done to biome: " + this.currentBiome.type() + "." +
                " Damage: " + totalDamage + ", " + previousHP + "->" + this.currentBiomeHP);
    }

    /**
     * Add a user if interacted with the 'mine' button to the currentUserMap with a timestamp
     *
     * @param userID User ID
     */
    public void updateCurrentUserMultiplication(String userID) {
        int oldMapSize = this.currentUserMap.size();
        this.currentUserMap.put(userID, LocalDateTime.now());
        updateCurrentUserMap();
        int newMapSize = this.currentUserMap.size();
        if (oldMapSize != newMapSize)
            adjustCurrentBiomeHP();
    }

    /**
     * Updates the currentUserMap by removing users from the list that did not interact with the 'mine' button longer
     * than some minutes ago
     */
    private void updateCurrentUserMap() {
        Iterator<Map.Entry<String, LocalDateTime>> iterator = this.currentUserMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, LocalDateTime> entry = iterator.next();
            LocalDateTime lastInteractionTime = entry.getValue();

            if (lastInteractionTime.isBefore(LocalDateTime.now().minusMinutes(Constants.MAX_MINE_NOT_INTERACTED_MINUTES))) {
                iterator.remove();
            }
        }
    }

    /**
     * Adjusts the biome HP depending on how many unique users interacted with the mine button in the last minutes
     */
    private void adjustCurrentBiomeHP() {
        int previousUserMultiplier = this.currentUserMultiplier;
        this.currentUserMultiplier = currentUserMap.size();

        this.currentBiomeFullHP = Main.generator.roundDouble(currentBiome.biomeHP() * currentUserMultiplier, 1);

        if (this.currentUserMultiplier < previousUserMultiplier) {
            double adjustment = (double) currentUserMultiplier / previousUserMultiplier;
            this.currentBiomeHP = Main.generator.roundDouble(this.currentBiomeHP * adjustment, 1);
        }
    }

    /**
     * Searches the item by the item name in the item list
     *
     * @param name The item name
     * @return Found item or null
     */
    public Item getItemByName(String name) {
        Item foundItem = null;
        for (Item item : itemList) {
            if (item.getName().replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))
                foundItem = item;
        }
        return foundItem;
    }

    /**
     * Gets an item from the itemList by an item ID
     *
     * @param id The item ID we search with
     * @return The item matching the item ID, if not found null is returned
     */
    public Item getItemByID(int id) {
        Item returnItem = null;
        for (Item item : itemList) {
            if (item.getID() == id)
                returnItem = item;
        }
        return returnItem;
    }

    /**
     * Gets a mob from the mob List by a mob ID
     *
     * @param id The mob ID we search with
     * @return The mob matching the mob ID, if not found null is returned
     */
    public Mob getMobByID(int id) {
        for (Mob mob : mobList) {
            if (mob.getID() == id)
                return mob;
        }
        return null;
    }

    public Biome getCurrentBiome() {
        return this.currentBiome;
    }

    public List<Mob> getMobList() {
        return this.mobList;
    }
}
