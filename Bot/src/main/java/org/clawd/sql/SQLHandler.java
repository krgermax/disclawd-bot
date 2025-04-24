package org.clawd.sql;

import org.clawd.main.Bot;
import org.clawd.main.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLHandler {

    public final SQLEmbeddedHandler sqlEmbeddedHandler = new SQLEmbeddedHandler();
    public final SQLInventoryHandler sqlInventoryHandler = new SQLInventoryHandler();
    public final SQLStatsHandler sqlStatsHandler = new SQLStatsHandler();

    /**
     * This method returns if a user ID can be found inside the table.
     *
     * @param userID The user ID
     * @return True or false, depending on if the user ID was found or not.
     */
    public boolean isUserRegistered(String userID) {
        boolean registered = false;
        try {
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "SELECT * FROM playertable WHERE userID = ?";
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
            Connection connection = Bot.getInstance().getSQLConnection();
            String sqlQuery = "INSERT INTO playertable (" +
                    "userID," +
                    " minedCount," +
                    " xpCount," +
                    " goldCount," +
                    " mobKills," +
                    " bossKills," +
                    " equipedItemID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
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
}
