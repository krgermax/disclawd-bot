package com.github.krgermax.data.mineworld;

import com.github.krgermax.buttons.ButtonManager;
import com.github.krgermax.data.MobSpawner;
import com.github.krgermax.data.blocks.Block;
import com.github.krgermax.data.blocks.BlockType;
import com.github.krgermax.data.items.Item;
import com.github.krgermax.data.mobs.Mob;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.utils.FileUpload;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Mineworld {

    private LocalDateTime timestamp;
    /*
        Mob spawner
     */
    private final MobSpawner mobSpawner;

    /*
        Block
     */
    private final List<Block> blockList;
    private Block currentBlock;
    private BlockType previousBlockType;

    /*
        Current user map
     */
    private final Map<String, LocalDateTime> currentUserMap;
    private int currentUserMultiplier;
    private final int MAX_MINE_NOT_INTERACTED_MINUTES = 2;


    public Mineworld(List<Block> blockList, List<Mob> mobList) {
        this.timestamp = LocalDateTime.now();

        this.blockList = blockList;
        this.currentBlock = generateBlock();
        this.previousBlockType = BlockType.VOID;

        this.currentUserMap = new HashMap<>();
        this.currentUserMultiplier = 1;

        this.mobSpawner = new MobSpawner(this, mobList);
    }

    /**
     * Selects a random biom from the blocks enum
     *
     * @return A biom
     */
    private Block generateBlock() {
        int size = blockList.size();
        int selector = (int) (Math.random() * size);

        Block returnBlock = blockList.get(selector);
        Main.LOGGER.info("The current block is: " + returnBlock.getType());
        /*
            The block is copied to avoid shared mutable state between Mineworld instances
         */
        return returnBlock.copy();
    }

    private EmbedBuilder buildBlockEmbed() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(currentBlock.getName())
                .setColor(Color.BLACK)
                .setDescription("Active miners: " + currentUserMap.size() + " (Last " + MAX_MINE_NOT_INTERACTED_MINUTES + " minutes)")
                .addField("Block HP", currentBlock.getCurrentHP() + "/" + currentBlock.getAdjustableFullHP(), false)
                .setImage("attachment://ore.png");
        return embedBuilder;
    }

    /**
     * This method replies to the '/block' command, by building an embedded message
     * with all necessary information and buttons
     *
     * @param event Event
     */
    public void replyWithBlockEmbedded(SlashCommandInteractionEvent event) {
        if (currentBlock.getType().equals(previousBlockType)) {
            event.replyEmbeds(buildBlockEmbed().build())
                    .addActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
        } else {
            event.replyEmbeds(buildBlockEmbed().build())
                    .addFiles(FileUpload.fromData(currentBlock.getImgFile(), "ore.png"))
                    .addActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
            previousBlockType = currentBlock.getType();
        }
    }

    /**
     * This method replies to the 'mine' button, by building an embedded message
     * with all necessary information and buttons. This method overloads the method
     * with the SlashCommandInteractionEvent event param
     *
     * @param event Event
     */
    public void replyWithBlockEmbedded(ButtonInteractionEvent event) {
        if (currentBlock.getType().equals(previousBlockType)) {
            event.replyEmbeds(buildBlockEmbed().build())
                    .addActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
        } else {
            event.replyEmbeds(buildBlockEmbed().build())
                    .addFiles(FileUpload.fromData(currentBlock.getImgFile(), "ore.png"))
                    .addActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
            previousBlockType = currentBlock.getType();
        }
    }

    /**
     * Updates the embedded message on a 'ButtonInteractionEvent'
     *
     * @param event Event
     */
    private void updateBlockMsg(ButtonInteractionEvent event) {
        if (currentBlock.getType().equals(previousBlockType)) {
            event.editMessageEmbeds(buildBlockEmbed().build())
                    .setActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
        } else {
            event.editMessageEmbeds(buildBlockEmbed().build())
                    .setFiles(FileUpload.fromData(currentBlock.getImgFile(), "ore.png"))
                    .setActionRow(Button.primary(ButtonManager.MINE_BUTTON_ID, Constants.MINE_BUTTON_EMOJI))
                    .queue();
            previousBlockType = currentBlock.getType();
        }
    }

    /**
     * Wrapper method to update the block on a 'ButtonInteractionEvent'
     * and the embedded message, such that the current state is displayed
     * correctly
     *
     * @param event        Event
     * @param equippedItem The equipped user item
     */
    public void updateBlock(ButtonInteractionEvent event, Item equippedItem) {
        currentBlock.damage(equippedItem);
        mobSpawner.spawnMob(event.getChannel());
        if (currentBlock.getCurrentHP() <= 0) {
            // Reset the old block and generate a new one
            previousBlockType = BlockType.VOID;
            currentBlock = generateBlock();
            updateBlockOnCompletion(event);
            /*
             * We need to update the mob spawner if a block is completed and only if it is completed
             * to update the spawnable mob list, avoids unnecessary list filtering calls
             */
            mobSpawner.updateSpawner();
            return;
        }
        updateBlockMsg(event);
    }

    /**
     * Updates the block state and embedded message on a 'ButtonInteractionEvent', if
     * the block has been completed by reaching HP of <= 0
     *
     * @param event Event
     */
    private void updateBlockOnCompletion(ButtonInteractionEvent event) {
        event.getMessage().delete().queue();
        /*
            Since this method is called after a new block is generated, we need to adjust its HP according to the amount
            of interacting users
         */
        adjustCurrentBlockHP();
        currentBlock.setCurrentHP(currentBlock.getAdjustableFullHP());
        replyWithBlockEmbedded(event);
        Main.LOGGER.info("Updated block because of completion.");
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
            adjustCurrentBlockHP();
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

            if (lastInteractionTime.isBefore(LocalDateTime.now().minusMinutes(MAX_MINE_NOT_INTERACTED_MINUTES))) {
                iterator.remove();
            }
        }
    }

    /**
     * Adjusts the block HP depending on how many unique users interacted with the mine button in a specific time window
     * <l>
     * <li>
     * If another user interacts and the current user map increases in size: <br>
     * This method only increases the adjustable full HP, but not the current block HP
     * </li>
     * <li>
     * If the current user map decreases: <br>
     * This method only decreases the current full HP, but not the adjustable full HP. As this happens in
     * {@link #updateBlockOnCompletion}
     * </li>
     * </l>
     */
    private void adjustCurrentBlockHP() {
        int previousUserMultiplier = this.currentUserMultiplier;
        this.currentUserMultiplier = currentUserMap.size();

        double adjustedHP = Main.generator.roundDouble(currentBlock.getTrueHP() * currentUserMultiplier, 1);
        /*
            If the user count increases I do not want to erase previous progress, therefore only max hp is adjusted
         */
        currentBlock.setAdjustableFullHP(adjustedHP);

        if (this.currentUserMultiplier < previousUserMultiplier) {
            double adjustment = (double) currentUserMultiplier / previousUserMultiplier;
            adjustedHP = Main.generator.roundDouble(currentBlock.getCurrentHP() * adjustment, 1);
            currentBlock.setCurrentHP(adjustedHP);
        }
    }

    public Block getCurrentBlock() {
        return currentBlock;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
