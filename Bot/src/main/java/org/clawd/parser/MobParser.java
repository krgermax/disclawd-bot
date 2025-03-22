package org.clawd.parser;

import org.clawd.data.mobs.BossMob;
import org.clawd.data.mobs.Mob;
import org.clawd.data.mobs.NormalMob;
import org.clawd.data.mobs.enums.MobSubType;
import org.clawd.data.mobs.enums.MobType;
import org.clawd.data.mobs.enums.TradeType;
import org.clawd.main.Main;
import org.clawd.parser.exceptions.FailedDataParseException;
import org.clawd.tokens.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    TODO: change up parser such that the .json file can be structured by mob types
          -> json array for normal, boss, trader mobs
 */
public class MobParser {

    private  final  Factory factory = new Factory();
    private final List<Integer> idList = new ArrayList<>();

    /**
     * Wrapper function to parse the Mobs out of a JSON file
     *
     * @return a list containing all mobs
     * @throws FailedDataParseException when parsing fails, either to invalid mobs or
     *                                   empty mob list
     */
    public List<Mob> parseMobs() throws FailedDataParseException {
        List<Mob> mobs = getMobsFromJSON();

        if (!validateMobs(mobs) || mobs.isEmpty())
            throw new FailedDataParseException(
                    "Could not parse mobs correctly:\n" +
                            "- valid mobs = " + validateMobs(mobs) + "\n" +
                            "- is mob list empty = " + mobs.isEmpty());

        Main.LOG.info("Mob parsing finished, mob list size: " + mobs.size());
        return mobs;
    }

    /**
     * The actual JSON parser itself for the mob JSON
     *
     * @return the list of parsed mobs
     */
    private List<Mob> getMobsFromJSON() {

        List<Mob> mobs = new ArrayList<>();

        try(FileReader fileReader = new FileReader(Constants.JSON_BASE_PATH + Constants.MOBS_JSON_FILEPATH)) {

            JSONObject obj = new JSONObject(new JSONTokener(fileReader));
            JSONArray arr = obj.getJSONArray(Constants.MOBS_JSON_MOBS);

            for (Object o : arr) {

                Mob mob;
                JSONObject jsonItem = (JSONObject) o;

                int mobID = jsonItem.getInt("id");
                String mobName = jsonItem.getString("name");
                String mobDesc = jsonItem.getString("description");
                MobType mobType = MobType.valueOf(jsonItem.getString("mob_type"));
                MobSubType mobSubType = MobSubType.valueOf(jsonItem.getString("mob_sub_type"));
                String fileName = jsonItem.getString("fileName");
                String imgPath = Constants.MOB_IMAGE_BASE_PATH + File.separator + fileName;
                double spawnChance = jsonItem.getDouble("spawn_chance");

                if (mobType.equals(MobType.NORMAL)) {
                    mob = getNormalMobFromJson(jsonItem, mobID, mobName, mobDesc, mobType, mobSubType, imgPath, spawnChance);

                } else if (mobType.equals(MobType.BOSS)) {
                    mob = getBossMobFromJson(jsonItem, mobID, mobName, mobDesc, mobType, mobSubType, imgPath, spawnChance);

                } else {
                    mob = getTradeMobFromJson(jsonItem, mobID, mobName, mobDesc, mobType, mobSubType, imgPath, spawnChance);
                }

                idList.add(mobID);
                mobs.add(mob);
            }

        } catch (JSONException | IOException ex) {
            Main.LOG.severe("Failed to parse JSON mobs file: " + ex.getMessage());
        }
        return mobs;
    }

    /**
     * Helper function to read normal mob object from JSON
     *
     * @return Normal mob
     */
    private Mob getNormalMobFromJson(JSONObject jsonItem, int mobID, String mobName, String mobDesc, MobType mobType, MobSubType mobSubType, String imgPath, double spawnChance) {

        double xpDropAmount = jsonItem.getDouble("xp_drop_amount");
        int goldDropAmount = jsonItem.getInt("gold_drop_amount");

        return factory.createNormalMob(
                mobID,
                mobName,
                mobDesc,
                mobType,
                mobSubType,
                imgPath,
                spawnChance,
                xpDropAmount,
                goldDropAmount
        );
    }

    /**
     * Helper function to read boss mob object from JSON
     *
     * @return Boss mob
     */
    private Mob getBossMobFromJson(JSONObject jsonItem, int mobID, String mobName, String mobDesc, MobType mobType, MobSubType mobSubType, String imgPath, double spawnChance) {

        double xpDropAmount = jsonItem.getDouble("xp_drop_amount");
        int goldDropAmount = jsonItem.getInt("gold_drop_amount");
        boolean specialDrop = jsonItem.getBoolean("special_drop");
        double health = jsonItem.getDouble("health_amount");

        return factory.createBossMob(
                mobID,
                mobName,
                mobDesc,
                mobType,
                mobSubType,
                imgPath,
                spawnChance,
                xpDropAmount,
                goldDropAmount,
                specialDrop,
                health
        );
    }

    /**
     * Helper function to read trade mob object from JSON
     *
     * @return Trade mob
     */
    private Mob getTradeMobFromJson(JSONObject jsonItem, int mobID, String mobName, String mobDesc, MobType mobType, MobSubType mobSubType, String imgPath, double spawnChance) {

        TradeType tradeType = TradeType.valueOf(jsonItem.getString("trade_type"));

        return factory.createTradeMob(
                mobID,
                mobName,
                mobDesc,
                mobType,
                mobSubType,
                imgPath,
                spawnChance,
                tradeType
        );
    }

    /**
     * Validates all mobs for some restrictions make help of the
     * isValidMob() method
     *
     * @param mobs The list of Mobs that needs to be validated
     * @return True or false, depending on validation
     */
    private boolean validateMobs(List<Mob> mobs) {
        for (Mob mob: mobs) {
            if (!isValidMob(mob))
                return false;
        }
        Main.LOG.info("Mob validation finished");
        return true;
    }

    /**
     * Checks the validity of exactly one mob
     *
     * @param mob The mob to be checked
     * @return True of false, depending on the mobs validity
     */
    private boolean isValidMob(Mob mob) {
        int uniqueID = mob.getID();
        if (uniqueID < 0
                || mob.getSpawnChance() < Constants.MOB_SPAWN_CHANCE_LOWER_B
                || mob.getSpawnChance() > Constants.MOB_SPAWN_CHANCE_UPPER_B
                || checkIsIDUnique(uniqueID)) {
            return false;
        }

        if (mob.getMobType().equals(MobType.NORMAL)) {

            NormalMob normalMob = (NormalMob) mob;
            double xpDrop = normalMob.getXpDrop();
            double goldDrop = normalMob.getGoldDrop();
            return xpDrop >= Constants.XP_DROP_AMOUNT_LOWER_B || goldDrop >= Constants.GOLD_DROP_AMOUNT_LOWER_B;

        } else if (mob.getMobType().equals(MobType.BOSS)) {
            BossMob bossMob = (BossMob) mob;
            double xpDrop = bossMob.getXpDrop();
            double goldDrop = bossMob.getGoldDrop();
            double health = bossMob.getHealth();
            return health > 0 || xpDrop >= Constants.XP_DROP_AMOUNT_LOWER_B || goldDrop >= Constants.GOLD_DROP_AMOUNT_LOWER_B;
        }

        File imageFile = new File(mob.getImgPath());
        if (!imageFile.exists()) {
            Main.LOG.severe("Mob image file not found: " + mob.getImgPath());
            return false;
        }

        return true;
    }

    private boolean checkIsIDUnique(int id) {
        return  idList.stream().filter(i -> i == id).count() >= 2;
    }
}
