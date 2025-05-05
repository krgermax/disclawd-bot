package com.github.krgermax.sql;

import com.github.krgermax.data.inventory.UserStats;
import com.github.krgermax.main.Main;
import com.github.krgermax.tokens.Constants;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SQLManager {

    private static SQLManager INSTANCE;

    public final SQLEmbeddedHandler sqlEmbeddedHandler = new SQLEmbeddedHandler();
    public final SQLInventoryHandler sqlInventoryHandler = new SQLInventoryHandler();
    public final SQLStatsHandler sqlStatsHandler = new SQLStatsHandler();

    private SQLManager() {}

    public static SQLManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SQLManager();
        }
        return INSTANCE;
    }

    /**
     * This method returns if a user ID can be found inside the table.
     *
     * @param userID The user ID
     * @return True or false, depending on if the user ID was found or not.
     */
    public boolean isUserRegistered(String userID) {
        boolean registered = false;
        try {
            Connection connection = Main.bot.getSQLConnection();
            String sqlQuery = "SELECT * FROM players WHERE "
                    + Constants.USER_ID_COLUMN_LABEL + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            registered = resultSet.next(); // If resultSet.next() is true, player is registered
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException ex) {
            Main.LOGGER.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return registered;
    }

    /**
     * This method registers a user if they are not registered yet. The user id is
     * entered into the table, all other values are initialized with default values.
     *
     * @param userID The user ID
     */
    public void registerUser(String userID) {
        try {
            Connection connection = Main.bot.getSQLConnection();
            String sqlQuery = "INSERT INTO players ("
                    + Constants.USER_ID_COLUMN_LABEL + ", "
                    + Constants.MINED_COLUMN_LABEL + ", "
                    + Constants.XP_COLUMN_LABEL + ", "
                    + Constants.GOLD_COLUMN_LABEL + ", "
                    + Constants.MOB_KILLS_COLUMN_LABEL + ", "
                    + Constants.BOSS_KILLS_COLUMN_LABEL + ", "
                    + Constants.EQUIPPED_ITEM_COLUMN_LABEL + ") "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            preparedStatement.setString(1, userID); // UserID
            preparedStatement.setInt(2, 0); // Default minedCount
            preparedStatement.setDouble(3, 0d); // Default xpCount
            preparedStatement.setInt(4, 0); // Default goldCount
            preparedStatement.setInt(5, 0); // Default mobKills
            preparedStatement.setInt(6, 0); // Default bossKills
            preparedStatement.setInt(7, -1); // Default equippedItemID

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                Main.LOGGER.info("Registered user with ID: " + userID);
            } else {
                Main.LOGGER.severe("Failed to register user. ");
            }
            preparedStatement.close();
        } catch (SQLException ex) {
            Main.LOGGER.severe("Some SQL error occurred: " + ex.getMessage());
        }
    }

    /**
     * This method retrieves the top 10 users in descending order filtered by a given category
     *
     * @param statQuery The filter to order by
     *
     * @return A list of User stats objects
     */
    public ArrayList<UserStats> getUsersRankedByCategory(String statQuery) {
        ArrayList<UserStats> userStatsList = new ArrayList<>();
        try {
            Connection connection = Main.bot.getSQLConnection();

            String sqlQuery = "SELECT " + Constants.USER_ID_COLUMN_LABEL + ", " + statQuery + ", " +
                    "RANK() OVER (ORDER BY " + statQuery + " DESC, " + Constants.USER_ID_COLUMN_LABEL + " ASC) AS rank " +
                    "FROM players " +
                    "ORDER BY " + statQuery + " DESC, " + Constants.USER_ID_COLUMN_LABEL + " ASC " +
                    "LIMIT 10";

            // no need to check for faulty sql queries, since the sqlQuery is user input mapped to actual columns
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                UserStats userStats = new UserStats();
                userStats.setUserID(resultSet.getString(Constants.USER_ID_COLUMN_LABEL));
                userStats.setRank(resultSet.getInt("rank"));

                setUserStats(statQuery, userStats, resultSet);

                userStatsList.add(userStats);
            }

        } catch (SQLException | IllegalStateException ex) {
            Main.LOGGER.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return userStatsList;
    }

    /**
     * This method retrieves a users stats based on a given category and the user id
     *
     * @param statQuery The filter to determine the users ranking
     * @param userID The user ID
     *
     * @return A user stat object with a set rank value
     */
    public UserStats getUserStatsRanked(String statQuery, String userID) {
        UserStats userStats = new UserStats();
        try {
            Connection connection = Main.bot.getSQLConnection();

            String sqlQuery = "SELECT ranked." + statQuery + ", ranked." + Constants.USER_ID_COLUMN_LABEL + ", ranked.rank " +
                    "FROM ( " +
                    "    SELECT " + Constants.USER_ID_COLUMN_LABEL + ", " + statQuery + ", " +
                    "           RANK() OVER (ORDER BY " + statQuery + " DESC, " + Constants.USER_ID_COLUMN_LABEL + " ASC) AS rank " +
                    "    FROM players " +
                    ") AS ranked " +
                    "WHERE ranked." + Constants.USER_ID_COLUMN_LABEL + " = ?";

            // no need to check for faulty sql queries, since the sqlQuery is user input mapped to actual columns
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                userStats.setUserID(userID);
                userStats.setRank(resultSet.getInt("rank"));
                setUserStats(statQuery, userStats, resultSet);
            }

        } catch (SQLException ex) {
            Main.LOGGER.severe("Some SQL error occurred: " + ex.getMessage());
        }
        return userStats;
    }

    private void setUserStats(String statQuery, UserStats userStats, ResultSet resultSet) throws SQLException {
        switch (statQuery) {
            case Constants.XP_COLUMN_LABEL:
                userStats.setXpCount(Main.generator.transformDouble(resultSet.getDouble(Constants.XP_COLUMN_LABEL))); // Fix precision issue
                break;
            case Constants.GOLD_COLUMN_LABEL:
                userStats.setGoldCount(resultSet.getInt(Constants.GOLD_COLUMN_LABEL));
                break;
            case Constants.MOB_KILLS_COLUMN_LABEL:
                userStats.setMobKills(resultSet.getInt(Constants.MOB_KILLS_COLUMN_LABEL));
                break;
            case Constants.MINED_COLUMN_LABEL:
                userStats.setMinedCount(resultSet.getInt(Constants.MINED_COLUMN_LABEL));
                break;
            default: throw new IllegalStateException("Unexpected value: " + statQuery);
        }
    }
}
