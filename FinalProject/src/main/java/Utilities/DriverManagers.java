package Utilities;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boggledApp.*;

/**
 * Class responsible for managing database connections and user authentication.
 */

public class DriverManagers {
    private static Connection con = null;
    private static String currentUsername;

    /**
     * Default constructor.
     */

    public DriverManagers() {
    }

    /**
     * Establishes a connection to the database.
     *
     * @return The database connection.
     */

    public static Connection getConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/boggled", "root", "");
        } catch (SQLException e) {
            System.out.println("No database found");
        }
        return con;
    }

    /**
     * Sets the current username.
     *
     * @param username The username to set.
     */

    // Method to set the current username
    public static void setCurrentUsername(String username) {
        currentUsername = username;
    }

    /**
     * Retrieves the current username.
     *
     * @return The current username.
     */

    // Method to retrieve the current username
    public static String getCurrentUsername() {
        System.out.println(currentUsername);
        return currentUsername;
    }

    /**
     * Authenticates the user with the provided credentials.
     *
     * @param username The username.
     * @param password The password.
     * @return The authentication status.
     * @throws SQLException               If an SQL exception occurs.
     * @throws bannedAccountException     If the account is banned.
     * @throws invalidCredentialsException If the credentials are invalid.
     */

    public static String logIn(String username, String password) throws SQLException, bannedAccountException, invalidCredentialsException {
        String query = "SELECT isBanned,username,password from user WHERE username = ? AND password = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String isBanned = rs.getString("isBanned");
                if (isBanned.equalsIgnoreCase("banned")) {
                    throw new bannedAccountException("Your account is banned!");
                } else {
                    setCurrentUsername(username);
                    return "not banned";
                }
            } else {
                throw new invalidCredentialsException("Invalid username or password!");
            }
        } catch (SQLException e) {
            System.out.println("Error during login");
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Validates the registration of a new user.
     *
     * @param username The username to validate.
     * @return True if the username is valid, false otherwise.
     */

    public static boolean validateRegistration(String username) {
        String query = "SELECT username FROM user WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new user.
     *
     * @param u The user object containing registration details.
     * @return True if registration is successful, false otherwise.
     * @throws usernameAlreadyExistsException If the username already exists.
     */

    public static boolean Register(user u) throws usernameAlreadyExistsException {
        boolean validate = validateRegistration(u.getUsername());
        if (!validate) {
            throw new usernameAlreadyExistsException("Username already exists");
        }
        String query = "INSERT INTO user(username, password, isBanned,avatar) VALUES (?, ?, ?,?)";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, u.getUsername());
            statement.setString(2, u.getPassword());
            statement.setString(3, "not Banned");
            statement.setInt(4, 1);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Error during register");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves the avatar of a player.
     *
     * @param username The username of the player.
     * @return The avatar of the player.
     */

    public static int getPlayerProfile(String username) {
        String query = "Select avatar from user where username=?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int avatar = rs.getInt(1);
                return avatar;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    /**
     * Updates the password of a user.
     *
     * @param username The username of the user whose password is to be updated.
     * @param password The new password.
     */

    public static void updateProfile(String username, String password) {
        String query = "UPDATE user set password=? where username= ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, password);
            statement.setString(2, username);
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the avatar of a player.
     *
     * @param username The username of the player.
     * @param avatar   The new avatar ID.
     * @return True if the avatar update is successful, false otherwise.
     */

    public static boolean updatePlayerAvatar(String username, int avatar) {
        String query = "UPDATE user set avatar=? where username= ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, avatar);
            statement.setString(2, username);
            statement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Retrieves a list of open rooms.
     *
     * @return An array of strings containing information about each open room.
     */

    public static String[] listOfRooms() {
        ArrayList<room> rooms = new ArrayList<room>();
        String query = "SELECT s.roomID, u.username FROM room r natural join score s inner join user u on r.host = u.userID WHERE r.status = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "open");
            ResultSet rs = statement.executeQuery();
            Map<Integer, room> roomMap = new HashMap<>();

            while (rs.next()) {
                int roomID = rs.getInt(1);
                String host = rs.getString(2);
                room room = roomMap.getOrDefault(roomID, new room(roomID, host, 0));
                room.incrementCount();
                roomMap.put(roomID, room);
            }
            rooms.addAll(roomMap.values());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String[] result = new String[rooms.size()];
        for (int i = 0; i < rooms.size(); i++) {
            room room = rooms.get(i);
            result[i] = room.getRoomID() + " | " + room.getCount() + " | " + room.getHost();
        }
        return result;
    }

    /**
     * Checks if a room ID exists.
     *
     * @param id The ID of the room to check.
     * @return True if the room ID exists, false otherwise.
     */

    public static boolean checkIDifExists(int id) {
        String query = "select * from room where roomID = ?";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return false;
            } else {
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a new room.
     *
     * @param id     The ID of the room to create.
     * @param userID The ID of the user who is the host of the room.
     * @return True if the room creation is successful, false otherwise.
     */

    public static boolean createRoom(int id, int userID) {
        String query = "INSERT INTO room(roomID,status,host) VALUES (?, ?,?)";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setString(2, "open");
            preparedStatement.setInt(3, userID);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows a player to join a room.
     *
     * @param id     The ID of the room to join.
     * @param userID The ID of the user joining the room.
     * @return True if the player successfully joins the room, false otherwise.
     */

    public static boolean joinPlayer(int id, int userID) {
        String query = "INSERT INTO score(userID,roomID) VALUES (?,?)";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, id);
            preparedStatement.execute();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Converts a username to its corresponding user ID.
     *
     * @param username The username to convert.
     * @return The user ID corresponding to the given username.
     */

    public static int convertToID(String username) {
        String query = "select userID from user where username = ?";
        try (Connection con = getConnection();
             PreparedStatement preparedStatement = con.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                throw new RuntimeException();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Allows a player to leave a room.
     *
     * @param roomID The ID of the room to leave.
     * @param userID The ID of the user leaving the room.
     * @return True if the player successfully leaves the room, false otherwise.
     */

    public static boolean leaveRoom(int roomID, int userID) {
        String query = "DELETE FROM score WHERE roomID = ? and userID =?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            statement.setInt(2, userID);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Cancels a room.
     *
     * @param roomID The ID of the room to cancel.
     * @return True if the room cancellation is successful, false otherwise.
     */

    public static boolean cancelRoom(int roomID) {
        String query = "DELETE FROM score WHERE roomID = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Stores generated characters for each round in a room.
     *
     * @param rounds An array containing generated characters for each round.
     * @param roomID The ID of the room to store the generated characters.
     */

    public static void storeGeneratedCharacters(String[] rounds, int roomID) {
        String query = "UPDATE room SET generatedLettersRound1 = ?, generatedLettersRound2 = ?, generatedLettersRound3 = ?, generatedLettersRound4 = ?, generatedLettersRound5 = ?, generatedLettersRound6 = ?, generatedLettersRound7 = ?,generatedLettersRound8=?,generatedLettersRound9 = ?, generatedLettersRound10 = ? WHERE roomID = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, rounds[0]);
            statement.setString(2, rounds[1]);
            statement.setString(3, rounds[2]);
            statement.setString(4, rounds[3]);
            statement.setString(5, rounds[4]);
            statement.setString(6, rounds[5]);
            statement.setString(7, rounds[6]);
            statement.setString(8, rounds[7]);
            statement.setString(9, rounds[8]);
            statement.setString(10, rounds[9]);
            statement.setInt(11, roomID);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves generated characters for each round in a room.
     *
     * @param roomID The ID of the room to retrieve generated characters for.
     * @return An array containing generated characters for each round in the room.
     */

    public static String[] getGeneratedCharacters(int roomID) {
        String query = "SELECT generatedLettersRound1, generatedLettersRound2, generatedLettersRound3,generatedLettersRound4,generatedLettersRound5,generatedLettersRound6,generatedLettersRound7, generatedLettersRound8,generatedLettersRound9,generatedLettersRound10 FROM room where roomID = ?";
        String[] generatedCharacters = new String[10];

        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                generatedCharacters[0] = resultSet.getString(1);
                generatedCharacters[1] = resultSet.getString(2);
                generatedCharacters[2] = resultSet.getString(3);
                generatedCharacters[3] = resultSet.getString(4);
                generatedCharacters[4] = resultSet.getString(5);
                generatedCharacters[5] = resultSet.getString(6);
                generatedCharacters[6] = resultSet.getString(7);
                generatedCharacters[7] = resultSet.getString(8);
                generatedCharacters[8] = resultSet.getString(9);
                generatedCharacters[9] = resultSet.getString(10);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return generatedCharacters;
    }

    /**
     * Closes a specific room.
     *
     * @param roomID The ID of the room to close.
     */

    public static void closeRoom(int roomID) {
        String query = "UPDATE room set status =?  WHERE roomID = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "close");
            statement.setInt(2, roomID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Closes all rooms.
     */

    public static void closeAllRoom() {
        String query = "UPDATE room set status = ? ";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "close");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves a list of all users.
     *
     * @return An ArrayList containing user objects representing all users in the database.
     */

    public static ArrayList<user> getUsers() {
        ArrayList<user> users = new ArrayList<>();
        String query = "SELECT username,highScore,isActive FROM user";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user u = new user(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                users.add(u);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches for users whose usernames match the given pattern.
     *
     * @param username The pattern to search for.
     * @return An ArrayList containing user objects representing users whose usernames match the given pattern.
     */

    public static ArrayList<user> searchUser(String username) {
        ArrayList<user> users = new ArrayList<>();
        String query = "SELECT username,highScore,isActive FROM user where lower(username) like ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                user u = new user(resultSet.getString(1), resultSet.getString(2), resultSet.getString(3));
                users.add(u);
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the online status of a user.
     *
     * @param username The username of the user to set online.
     */

    public static void setOnline(String username) {
        String query = "UPDATE user SET isActive = ? WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "online");
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the offline status of a user.
     *
     * @param username The username of the user to set offline.
     */

    public static void setOffline(String username) {
        String query = "UPDATE user SET isActive = ? WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "offline");
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the offline status for all users.
     */

    public static void setAllOffline() {
        String query = "UPDATE user SET isActive = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, "offline");
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores the high score of a single player.
     *
     * @param username  The username of the player.
     * @param highScore The high score to store.
     */

    public static void storeHighScoreSinglePlayer(String username, int highScore) {
        String query = "UPDATE user \n" +
                "SET highscore = ?\n" +
                "WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, highScore);
            statement.setString(2, username);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores the score of a player for a specific round in a multiplayer game.
     *
     * @param username    The username of the player.
     * @param score       The score of the round.
     * @param roundNumber The number of the round.
     * @param roomID      The ID of the room.
     */

    public static void storeRoundScore(String username, int score, int roundNumber, int roomID) {
        String query = "UPDATE score s\n" +
                "INNER JOIN user u ON s.userID = u.userID\n" +
                "SET s.round" + roundNumber + " = ?\n" +
                "WHERE u.username = ? AND s.roomID = ?;\n";
        try (Connection con = getConnection();
             PreparedStatement statement= con.prepareStatement(query)) {
            statement.setInt(1, score);
            statement.setString(2, username);
            statement.setInt(3, roomID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the high score of a user.
     *
     * @param username The username of the user.
     * @return The high score of the user.
     */

    public static int getHighScore(String username) {
        System.out.println("hs user:" + username);
        String query = "SELECT highScore \n" +
                "FROM user \n" +
                "WHERE username = ? ;";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int highScore = rs.getInt(1); // Assuming highScore is stored in the first column (index 1)
                return highScore;
            } else {
                // Return a meaningful value when no record is found (e.g., -1)
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stores the total score of a player in a multiplayer game.
     *
     * @param username   The username of the player.
     * @param totalScore The total score to store.
     * @param roomID     The ID of the room.
     */

    public static void storeScoreMultiPlayer(String username, int totalScore, int roomID) {
        String query = "UPDATE score \n" +
                "SET totalScore = ? \n" +
                "WHERE userID = (select userID from user where username = ?)\n" +
                "AND roomID = ?;";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, totalScore);
            statement.setString(2, username);
            statement.setInt(3, roomID);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param username The username of the user to delete.
     * @return True if the user is successfully deleted, false otherwise.
     */

    public static boolean deleteUser(String username) {
        String query = "DELETE FROM user WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the current game leaderboard for a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the leaderboard entries.
     */

    public static String[] getCurrentGameLeaderboard(int roomID) {
        System.out.println("leader board:" + roomID);
        List<String> leaderboard = new ArrayList<>();
        String query = "SELECT u.username, \n" +
                "SUM(COALESCE(s.round1, 0) + COALESCE(s.round2, 0) + \n" +
                "COALESCE(s.round3, 0) + COALESCE(s.round4, 0) + COALESCE(s.round5, 0) + COALESCE(s.round6, 0) + COALESCE(s.round7, 0)) as totalScore, \n" +
                "u.avatar \n" +
                "FROM user u INNER JOIN score s ON u.userID = s.userID\n" +
                "WHERE s.roomID = ? \n" +
                "GROUP BY u.username \n" +
                "ORDER BY totalScore DESC"; // Order by totalScore in descending order
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int totalScore = rs.getInt(2);
                int avatar = rs.getInt(3);
                leaderboard.add("Username:" + username + "| TotalScore:" + totalScore + "| Avatar:" + avatar + "|");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboard.toArray(new String[0]);
    }

    /**
     * Retrieves the leaderboard for the menu displaying top players.
     *
     * @return An array containing strings representing the leaderboard entries.
     */

    public static String[] menuLeaderboard() {
        List<String> leaderboard = new ArrayList<>();
        String query = "SELECT \n" +
                "    u.username, \n" +
                "    COUNT(s.totalScore) AS wins\n" +
                "FROM \n" +
                "    user u\n" +
                "INNER JOIN \n" +
                "    score s ON u.userID = s.userID\n" +
                "WHERE \n" +
                "    s.totalScore > 0  \n" +
                "GROUP BY \n" +
                "    u.username\n" +
                "ORDER BY \n" +
                "    wins DESC\n" +
                "LIMIT 5;\n";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int totalScore = rs.getInt(2);
                leaderboard.add(username + ":" + totalScore);
                System.out.println(username + totalScore);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return leaderboard.toArray(new String[0]);
    }

    /**
     * Checks if there are more than one player in a room.
     *
     * @param roomID The ID of the room.
     * @return True if there are more than one player in the room, otherwise false.
     */

    public static boolean checkPlayers(int roomID) {
        String query = "SELECT count(userID) FROM score where roomID = ?;";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            int totalPlayers = 0;
            while (rs.next()) {
                totalPlayers = rs.getInt(1);
            }
            if (totalPlayers > 1) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates the username of a user.
     *
     * @param oldUsername The old username.
     * @param newUsername The new username.
     * @return True if the username is successfully updated, otherwise false.
     * @throws usernameAlreadyExistsException if the new username already exists.
     */

    public static boolean updateUsername(String oldUsername, String newUsername) throws usernameAlreadyExistsException {
        String query = "UPDATE user SET username = ? WHERE username = ?";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setString(1, newUsername);
            statement.setString(2, oldUsername);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new usernameAlreadyExistsException("username: " + newUsername + " already exists");
        }
    }

    /**
     * Retrieves the scores for round 1 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 1.
     */

    public static String[] getRound1Score(int roomID) {
        List<String> round1Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round1 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r1Score = rs.getInt(2);
                round1Scores.add("Username:" + username + "| Round1Score:" + r1Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round1Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 2 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 2.
     */

    public static String[] getRound2Score(int roomID) {
        List<String> round1Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round2 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r1Score = rs.getInt(2);
                round1Scores.add("Username:" + username + "| Round2Score:" + r1Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round1Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 3 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 3.
     */

    public static String[] getRound3Score(int roomID) {
        List<String> round1Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round3 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r1Score = rs.getInt(2);
                round1Scores.add("Username:" + username + "| Round3Score:" + r1Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round1Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 4 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 4.
     */

    public static String[] getRound4Score(int roomID) {
        List<String> round4Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round4 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r1Score = rs.getInt(2);
                round4Scores.add("Username:" + username + "| Round4Score:" + r1Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round4Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 5 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 5.
     */

    public static String[] getRound5Score(int roomID) {
        List<String> round5Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round5 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r5Score = rs.getInt(2);
                round5Scores.add("Username:" + username + "| Round5Score:" + r5Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round5Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 6 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 6.
     */

    public static String[] getRound6Score(int roomID) {
        List<String> round6Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round6 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r6Score = rs.getInt(2);
                round6Scores.add("Username:" + username + "| Round6Score:" + r6Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round6Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 7 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 7.
     */

    public static String[] getRound7Score(int roomID) {
        List<String> round7Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round7 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r7Score = rs.getInt(2);
                round7Scores.add("Username:" + username + "| Round7Score:" + r7Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round7Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 8 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 8.
     */

    public static String[] getRound8Score(int roomID) {
        List<String> round8Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round8 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r8Score = rs.getInt(2);
                round8Scores.add("Username:" + username + "| Round8Score:" + r8Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round8Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 9 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 9.
     */

    public static String[] getRound9Score(int roomID) {
        List<String> round9Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round9 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r9Score = rs.getInt(2);
                round9Scores.add("Username:" + username + "| Round9Score:" + r9Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round9Scores.toArray(new String[0]);
    }

    /**
     * Retrieves the scores for round 10 in a specific room.
     *
     * @param roomID The ID of the room.
     * @return An array containing strings representing the scores for round 10.
     */

    public static String[] getRound10Score(int roomID) {
        List<String> round10Scores = new ArrayList<>();
        String query = "SELECT u.username, s.round10 FROM score s \n" +
                "INNER JOIN user u ON u.userID = s.userID\n" +
                "WHERE roomID = ? ORDER BY 2 DESC";
        try (Connection con = getConnection();
             PreparedStatement statement = con.prepareStatement(query)) {
            statement.setInt(1, roomID);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String username = rs.getString(1);
                int r10Score = rs.getInt(2);
                round10Scores.add("Username:" + username + "| Round10Score:" + r10Score);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return round10Scores.toArray(new String[0]);
    }
}