package org.clawd.data;

import org.clawd.data.biomes.Biome;
import org.clawd.data.biomes.BiomeType;
import org.clawd.main.Main;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Generator {

    private final Set<BiomeType> xpBiomeTypes;

    public Generator() {
        this.xpBiomeTypes = Main.mineworld.getBiomeList().stream()
                .filter(Biome::isXpEnabled)
                .map(Biome::getType)
                .collect(Collectors.toSet());
    }

    /**
     * Used to generate XP
     *
     * @return XP as double
     */
    public double generateXP() {
        BiomeType biomeType = Main.mineworld.getCurrentBiome().getType();

        if (!xpBiomeTypes.contains(biomeType))
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
     * Compute level from XP 'XP = (level/0.07)^2'
     *
     * @param XP XP
     */
    public int computeLevel(double XP) {
        return (int) transformDouble((0.07 * Math.sqrt(XP)));
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
