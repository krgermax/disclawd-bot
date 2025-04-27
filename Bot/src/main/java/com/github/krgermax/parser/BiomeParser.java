package com.github.krgermax.parser;

import com.github.krgermax.data.biomes.Biome;
import com.github.krgermax.data.biomes.BiomeType;
import com.github.krgermax.data.mobs.enums.MobSubType;
import com.github.krgermax.main.Main;
import com.github.krgermax.parser.exceptions.FailedDataParseException;
import com.github.krgermax.tokens.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BiomeParser {

    /**
     * Wrapper function to parse the Biomes out of a JSON file
     *
     * @return a list containing all items
     * @throws FailedDataParseException when parsing fails, either to invalid items or
     *                                  empty item list
     */
    public List<Biome> parseBiomes() throws FailedDataParseException {
        List<Biome> biomes = getBiomesFromJSON();

        if (!validateBiomes(biomes) || biomes.isEmpty())
            throw new FailedDataParseException(
                    "Could not parse biomes correctly:\n" +
                            "- valid biomes = " + validateBiomes(biomes) + "\n" +
                            "- is biome list empty = " + biomes.isEmpty());

        Main.LOGGER.info("Biome parsing finished, biome list size: " + biomes.size());
        return biomes;
    }

    /**
     * The actual JSON parser itself for the biome JSON
     *
     * @return the list of parsed biomes
     */
    private List<Biome> getBiomesFromJSON() {
        List<Biome> biomeList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(Constants.JSON_BASE_PATH + Constants.BIOMES_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(Constants.BIOMES_JSON_BIOMES);

            for (Object o : arr) {

                JSONObject jsonItem = (JSONObject) o;

                String name = jsonItem.getString("name");
                BiomeType biomeType = BiomeType.valueOf(jsonItem.getString("type"));
                double biomeHP = jsonItem.getDouble("hp");
                String fileName = jsonItem.getString("fileName");
                String imgPath = Constants.BIOME_IMAGE_BASE_PATH + File.separator + fileName;
                boolean xpEnabled = jsonItem.getBoolean("xpEnabled");
                JSONArray jsonArray = jsonItem.getJSONArray("mobs");
                Set<MobSubType> spawnableMobs = new HashSet<>();

                for (Object object : jsonArray) {
                    spawnableMobs.add(MobSubType.valueOf((String) object));
                }

                biomeList.add(new Biome(name, biomeType, biomeHP, imgPath, xpEnabled, spawnableMobs));
            }
        } catch (JSONException | IOException ex) {
            Main.LOGGER.severe("Failed to parse JSON biomes file: " + ex.getMessage());
        }

        return biomeList;
    }

    /**
     * Validates all biomes for some restrictions make help of the
     * isValidBiome() method
     *
     * @param biomes The list of Biomes that needs to be validated
     * @return True or false, depending on validation
     */
    private boolean validateBiomes(List<Biome> biomes) {
        for (Biome biome : biomes) {
            if (!isValidBiome(biome))
                return false;
        }
        Main.LOGGER.info("Biome validation finished");
        return true;
    }

    /**
     * Checks the validity of exactly one biome
     *
     * @param biome The biome to be checked
     * @return True of false, depending on the biomes validity
     */
    private boolean isValidBiome(Biome biome) {
        if (biome.getTrueHP() <= 0) {
            return false;
        }
        File imageFile = new File(biome.getImgPath());
        if (!imageFile.exists()) {
            Main.LOGGER.severe("Biome image file not found: " + biome.getImgPath());
            return false;
        }
        return true;
    }
}
