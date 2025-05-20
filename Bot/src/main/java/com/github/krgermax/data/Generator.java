package com.github.krgermax.data;

import com.github.krgermax.data.blocks.Block;
import com.github.krgermax.data.blocks.BlockType;
import com.github.krgermax.data.mineworld.Mineworld;
import com.github.krgermax.main.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {

    private static Generator INSTANCE;
    private final Set<BlockType> xpBlockTypes;

    private Generator() {
        this.xpBlockTypes = Main.mineworldManager.getBlocks().stream()
                .filter(Block::isXpEnabled)
                .map(Block::getType)
                .collect(Collectors.toSet());
    }

    public static Generator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Generator();
        }
        return INSTANCE;
    }

    /**
     * Used to generate XP
     *
     * @return XP as double
     */
    public double generateXP(Mineworld mineworld) {
        BlockType blockType = mineworld.getCurrentBlock().getType();

        if (!xpBlockTypes.contains(blockType))
            return 0d;

        double generatedXP = (Math.random() * 4) + 1;
        double transformedXP = transformDouble(generatedXP);
        Main.LOGGER.info("Generated XP: " + transformedXP);
        return transformedXP;
    }

    /**
     * Used to generate gold
     *
     * @return Gold as int
     */
    public int generateGold() {
        int generatedGold = (int) (Math.random() * 3) + 1;
        Main.LOGGER.info("Generated gold: " + generatedGold);
        return generatedGold;
    }

    /**
     * Transforms a double to form X.X, by cutting of decimal places
     *
     * @param initialValue An initial double
     * @return The double after the transformation
     */
    public double transformDouble(double initialValue) {
        return ((int) (initialValue * 10)) / 10d;
    }

    /**
     * Compute level from XP 'level = (0.07 * sqrt(XP)) '
     *
     * @param XP XP
     */
    public int computeLevel(double XP) {
        return (int) transformDouble((0.07 * Math.sqrt(XP)));
    }

    /**
     * Computes XP from level 'XP = (level/0.07)^2'
     *
     * @param level The level to convert into XP
     *
     * @return The needed XP for the provided level
     */
    public double computeXP(int level) {
        return transformDouble((level/0.07) * (level/0.07));
    }

    /**
     * Rounds a double
     *
     * @param value  The double value
     * @param places How many places to round
     * @return The rounded double
     */
    public double roundDouble(double value, int places) {
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
