package com.github.krgermax.parser;

import com.github.krgermax.data.blocks.Block;
import com.github.krgermax.data.blocks.BlockType;
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

public class BlockParser {

    public static final String BLOCK_IMAGE_BASE_PATH = "src" + File.separator + "main" + File.separator + "resources" + File.separator + "images" + File.separator + "blocks";
    public static final String BLOCK_JSON_FILEPATH = File.separator + "blocks.json";
    public static final String BLOCK_JSON_BLOCKS = "blocks";

    /**
     * Wrapper function to parse the blocks out of a JSON file
     *
     * @return a list containing all items
     * @throws FailedDataParseException when parsing fails, either to invalid items or
     *                                  empty item list
     */
    public List<Block> parseBlocks() throws FailedDataParseException {
        List<Block> blocks = getBlocksFromJSON();

        boolean valid = validateBlocks(blocks);
        if (!valid || blocks.isEmpty())
            throw new FailedDataParseException(
                    "Could not parse blocks correctly:\n" +
                            "- valid blocks = " + valid + "\n" +
                            "- is block list empty = " + blocks.isEmpty());

        Main.LOGGER.info("Block parsing finished, block list size: " + blocks.size());
        return blocks;
    }

    /**
     * The actual JSON parser itself for the block JSON
     *
     * @return the list of parsed blocks
     */
    private List<Block> getBlocksFromJSON() throws FailedDataParseException {
        List<Block> blockList = new ArrayList<>();

        try (FileReader fileReader = new FileReader(Constants.JSON_BASE_PATH + BLOCK_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(BLOCK_JSON_BLOCKS);

            for (Object o : arr) {

                JSONObject jsonItem = (JSONObject) o;

                String name = jsonItem.getString("name");
                BlockType blockType = BlockType.valueOf(jsonItem.getString("type"));
                double blockHP = jsonItem.getDouble("hp");
                String fileName = jsonItem.getString("fileName");
                String imgPath = BLOCK_IMAGE_BASE_PATH + File.separator + fileName;
                boolean xpEnabled = jsonItem.getBoolean("xpEnabled");
                JSONArray jsonArray = jsonItem.getJSONArray("mobs");
                Set<MobSubType> spawnableMobs = new HashSet<>();

                for (Object object : jsonArray) {
                    spawnableMobs.add(MobSubType.valueOf((String) object));
                }

                blockList.add(MineFactory.createBlock(name, blockType, blockHP, imgPath, xpEnabled, spawnableMobs));
            }
        } catch (JSONException | IOException | IllegalArgumentException ex) {
            Main.LOGGER.error("Failed to parse JSON blocks file: " + ex.getMessage());
            throw new FailedDataParseException("Failed to parse JSON blocks file: " + ex.getMessage());
        }

        return blockList;
    }

    /**
     * Validates all blocks for some restrictions make help of the
     * isValidBlock() method
     *
     * @param blocks The list of blocks that needs to be validated
     * @return True or false, depending on validation
     */
    private boolean validateBlocks(List<Block> blocks) {
        for (Block block : blocks) {
            if (!isValidBlock(block))
                return false;
        }
        Main.LOGGER.info("Block validation finished");
        return true;
    }

    /**
     * Checks the validity of exactly one block
     *
     * @param block The block to be checked
     * @return True of false, depending on the blocks validity
     */
    private boolean isValidBlock(Block block) {
        if (block.getTrueHP() <= 0) {
            return false;
        }
        if (!block.getImgFile().exists()) {
            Main.LOGGER.error("Block image file not found: " + block.getImgFile().getPath());
            return false;
        }
        return true;
    }
}
