package Server;

import Utilities.DriverManagers;
import Utilities.user;
import View.AdminGUI;
import boggledApp.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.*;

public class servant extends BoggledPOA {
    private static final Object lock = new Object();
    private static final Map<String, Boolean> activeSessions = new HashMap<>();
    private Map<String, BoggledCallBack> clientCallbacks = new HashMap<>();
    private static BufferedReader br = null;
    private static final String CONSONANTS = "bcdfghjklmnpqrstvwxyz";
    private static final String VOWELS = "aeiou";
    private static Random random = new Random();
    private static final Map<Integer, Integer> countdowns = new HashMap<>();
    private static final Map<Integer, ScheduledExecutorService> roomSchedulers = new HashMap<>();
    private static final Map<String, Map<String, Integer>> roundCountMap = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Map<String, Set<String>>> submittedWordsPerRoom = new ConcurrentHashMap<>();
    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private static int gameScore;

      /**
     * Registers a new user with the provided username and password.
     * <p>
     * This method creates a new user object with the given username and password,
     * then attempts to register the user using the DriverManagers class.
     * The registration process is synchronized to ensure thread safety.
     * </p>
     *
     * @param username The username of the user to be registered.
     * @param password The password of the user to be registered.
     * @return {@code true} if the registration is successful, {@code false} otherwise.
     * @throws usernameAlreadyExistsException If the provided username already exists in the system.
     */

    @Override
    public boolean register(String username, String password) throws usernameAlreadyExistsException {
        synchronized (lock) {
            user u = new user(username, password);
            boolean register = DriverManagers.Register(u);
            return register;
        }
    }

     /**
     * Logs in a user with the provided username and password.
     * <p>
     * This method attempts to log in a user with the given username and password.
     * It first checks if the user credentials are valid using the DriverManagers class.
     * If the credentials are valid and the user is not already logged in, it creates a new session
     * and sets the user as online. The login process is synchronized to ensure thread safety.
     * </p>
     *
     * @param username The username of the user trying to log in.
     * @param password The password of the user trying to log in.
     * @return {@code true} if the login is successful, {@code false} otherwise.
     * @throws invalidCredentialsException If the provided credentials are invalid.
     * @throws userAlreadyLogInException  If the user is already logged in.
     * @throws bannedAccountException     If the user's account is banned.
     */

    @Override
    public boolean login(String username, String password) throws invalidCredentialsException, userAlreadyLogInException, bannedAccountException {
        synchronized (lock) {
            try {
                DriverManagers.logIn(username, password);
                if (!validateSession(username)) {
                    DriverManagers.setCurrentUsername(username);
                    createSession(username);
                    DriverManagers.setOnline(username);
                    return true;
                } else {
                    throw new userAlreadyLogInException("Account is already online!");
                }
            } catch (SQLException ignore) {
            }
            return false;
        }
    }

     /**
     * Verifies a word submitted by a player in a specific room.
     * <p>
     * This method checks whether a submitted word is valid and unique within the specified room.
     * It first initializes the map for submitted words per room if it doesn't exist already.
     * Then, it adds the submitted word to the user's set of submitted words for the room.
     * If the word is not valid or has already been submitted by another player in the room,
     * it throws an invalidWordException.
     * </p>
     *
     * @param roomID   The ID of the room where the word is being submitted.
     * @param word     The word submitted by the player.
     * @param username The username of the player submitting the word.
     * @return {@code true} if the word is valid and unique, {@code false} otherwise.
     * @throws invalidWordException If the submitted word is invalid or not unique.
     */

    @Override
    public synchronized boolean verifyWord(int roomID, String word, String username) throws invalidWordException {
        // Initialize the map for submitted words per room if it doesn't exist
        submittedWordsPerRoom.putIfAbsent(roomID, new ConcurrentHashMap<>());
        Map<String, Set<String>> userWordsMap = submittedWordsPerRoom.get(roomID);
        userWordsMap.putIfAbsent(username, new HashSet<>());
        userWordsMap.get(username).add(word.toLowerCase());

        // Check if the word is valid
        if (!checkWordIfValid(word)) {
            System.out.println(word + " is invalid");
            throw new invalidWordException("Invalid word");
        }

        // Check if the word has already been submitted by another player
        for (Map.Entry<String, Set<String>> entry : userWordsMap.entrySet()) {
            if (!entry.getKey().equals(username) && entry.getValue().contains(word.toLowerCase())) {
                System.out.println(word + " has already been submitted by another player in room ID: " + roomID);
                return false;
            }
        }
        System.out.println(word + " is valid and unique for " + username + " in room ID: " + roomID);
        return true;
    }

    /**
     * Retrieves the score of a player.
     * <p>
     * This method retrieves the score of the player with the specified username.
     * The score is returned as an integer value.
     * </p>
     *
     * @param username The username of the player whose score is to be retrieved.
     * @param earnedPts The earned points to be added to the player's score.
     * @return The score of the player.
     */

    @Override
    public int getPlayerScore(String username, int earnedPts) {
        return 0;
    }

    /**
     * Calculates the score based on earned points.
     * <p>
     * This method calculates the score based on the earned points.
     * It takes the earned points as input and returns the calculated score as an integer value.
     * </p>
     *
     * @param earnedPts The earned points for which the score is to be calculated.
     * @return The calculated score.
     */

    @Override
    public int calculateScore(int earnedPts) {
        return 0;
    }

    /**
     * Creates a set of letters for a single-player game.
     * <p>
     * This method generates a set of 20 letters for a single-player game.
     * The generated letters are a combination of consonants and vowels.
     * The letters are returned as an array of strings.
     * </p>
     *
     * @return An array of strings representing the generated letters.
     */

    @Override
    public String[] createGameSinglePlayer() {
        String[] letters = new String[20];
        letters = generateCharacters();
        System.out.println(letters);
        return letters;
    }

    /**
     * Creates a multiplayer game room and initializes the game.
     * <p>
     * This method creates a multiplayer game room for the specified username.
     * It generates unique IDs for the room and the user, creates the room,
     * starts the game countdown, and joins the player to the room.
     * It generates characters for each round of the game and stores them.
     * The method returns an array containing the room ID and the remaining time for the countdown.
     * </p>
     *
     * @param username The username of the player initiating the game.
     * @return An array containing the room ID and the remaining time for the countdown.
     */

    @Override
    public synchronized int[] createGameMultiPlayer(String username) {
        int userID = DriverManagers.convertToID(username);
        int roomID = generateUniqueID();
        DriverManagers.createRoom(roomID, userID);
        startGameCountdown(roomID);
        DriverManagers.joinPlayer(roomID, userID);
        String[] round1Letters = generateCharacters();
        String[] round2Letters = generateCharacters();
        String[] round3Letters = generateCharacters();
        String[] round4Letters = generateCharacters();
        String[] round5Letters = generateCharacters();
        String[] round6Letters = generateCharacters();
        String[] round7Letters = generateCharacters();
        String[] round8Letters = generateCharacters();
        String[] round9Letters = generateCharacters();
        String[] round10Letters = generateCharacters();
        String[] allRounds = {String.join("", round1Letters), String.join("", round2Letters), String.join("", round3Letters), String.join("", round4Letters), String.join("", round5Letters) ,String.join("", round6Letters), String.join("", round7Letters),String.join("", round8Letters),String.join("", round9Letters),String.join("", round10Letters)};
        DriverManagers.storeGeneratedCharacters(allRounds,roomID);
        int remainingTime = countdowns.getOrDefault(roomID, 0);
        return new int[]{roomID, remainingTime};
    }

    /**
     * Retrieves the generated characters for a specified room.
     * <p>
     * This method retrieves the generated characters for a specified multiplayer game room.
     * It returns the characters as an array of strings.
     * </p>
     *
     * @param roomID The ID of the room for which to retrieve the characters.
     * @return An array of strings containing the generated characters.
     */

    @Override
    public synchronized String[] getCharacters(int roomID) {
        return DriverManagers.getGeneratedCharacters(roomID);
    }

    /**
     * Allows a player to leave a multiplayer game room.
     * <p>
     * This method allows a player to leave a multiplayer game room.
     * It removes the player from the room and returns a boolean indicating whether the operation was successful.
     * </p>
     *
     * @param roomID   The ID of the room from which the player wants to leave.
     * @param username The username of the player leaving the room.
     * @return {@code true} if the player successfully leaves the room, {@code false} otherwise.
     */

    @Override
    public synchronized boolean leaveRoom(int roomID, String username) {
        int userID = DriverManagers.convertToID(username);
        return DriverManagers.leaveRoom(roomID, userID);
    }

    /**
     * Cancels a multiplayer game room.
     * <p>
     * This method cancels the specified multiplayer game room.
     * It shuts down the scheduler associated with the room and removes it from the list of active rooms.
     * It also triggers the cancellation of the room in the database.
     * </p>
     *
     * @param roomID The ID of the room to cancel.
     * @return {@code true} if the room is successfully canceled, {@code false} otherwise.
     */

    @Override
    public synchronized boolean cancelRoom(int roomID) {
        ScheduledExecutorService scheduler = roomSchedulers.get(roomID);
        if (scheduler != null) {
            scheduler.shutdown();
            roomSchedulers.remove(roomID);
        }
        return DriverManagers.cancelRoom(roomID);
    }

    /**
     * Starts the game.
     * <p>
     * This method starts the game by initiating the first round.
     * It returns the characters for the first round.
     * </p>
     *
     * @return An array of strings containing the characters for the first round.
     */

    @Override
    public String[] startGame() {
        return null;
    }

    /**
     * Ends the game.
     * <p>
     * This method ends the ongoing game.
     * </p>
     */

    @Override
    public void endGame() {
    }

    /**
     * Handles a player leaving the game.
     * <p>
     * This method handles a player leaving the ongoing game.
     * </p>
     */

    @Override
    public void playerLeft() {
    }

    /**
     * Shows the list of active game rooms.
     * <p>
     * This method retrieves and returns the list of active game rooms from the database.
     * </p>
     *
     * @return An array of strings containing information about the active game rooms.
     */

    @Override
    public String[] showListOfRooms() {
        return DriverManagers.listOfRooms();
    }

    /**
     * Allows a player to join a multiplayer game room.
     * <p>
     * This method allows a player to join the specified multiplayer game room.
     * It verifies the room's existence and checks if the countdown has not finished.
     * If the room doesn't exist or the countdown has finished, it throws a roomDoesntExistException.
     * Otherwise, it adds the player to the room and starts the game countdown if necessary.
     * </p>
     *
     * @param roomId   The ID of the room to join.
     * @param username The username of the player joining the room.
     * @return The remaining time for the game countdown.
     * @throws roomDoesntExistException If the room doesn't exist or the countdown has finished.
     */

    @Override
    public int inputRoom(int roomId, String username) throws roomDoesntExistException {
        int userID = DriverManagers.convertToID(username);
        synchronized (this) {
            boolean verify = DriverManagers.joinPlayer(roomId, userID);
            if (!verify) {
                throw new roomDoesntExistException("Room doesn't exist");
            }

            int remainingTime = countdowns.getOrDefault(roomId, 0);
            if (remainingTime <= 0) {
                throw new roomDoesntExistException("Room doesn't exist or the countdown has already finished.");
            }

            if (!roomSchedulers.containsKey(roomId)) {
                startGameCountdown(roomId);
            }

            return remainingTime;
        }
    }

    /**
     * Notifies the server that a player has joined a game room.
     * <p>
     * This method notifies the server that a player has successfully joined a game room.
     * </p>
     *
     * @param username The username of the player who joined the room.
     */

    @Override
    public void playerJoined(String username) {
    }

    /**
     * Retrieves the avatar of a player.
     * <p>
     * This method retrieves the avatar of the player with the specified username.
     * </p>
     *
     * @param username The username of the player whose avatar is to be retrieved.
     * @return The avatar of the player.
     */

    @Override
    public int getPlayerAvatar(String username) {
        return DriverManagers.getPlayerProfile(username);
    }

    /**
     * Retrieves the avatar of a player.
     * <p>
     * This method retrieves the avatar of the player with the specified username.
     * </p>
     *
     * @param username The username of the player whose avatar is to be retrieved.
     * @return The avatar of the player.
     */

    @Override
    public boolean updatePlayerAvatar(String username, int avatar) {
        return DriverManagers.updatePlayerAvatar(username, avatar);
    }

    /**
     * Updates the password of a player.
     * <p>
     * This method updates the password of the player with the specified username.
     * </p>
     *
     * @param username The username of the player whose password is to be updated.
     * @param password The new password for the player.
     * @return {@code true} if the password is successfully updated, {@code false} otherwise.
     */

    @Override
    public boolean updateCredential(String username, String password) {
        DriverManagers.updateProfile(username, password);
        return true;
    }

    /**
     * Retrieves the leaderboard.
     * <p>
     * This method retrieves the leaderboard from the database.
     * </p>
     *
     * @return An array of strings representing the leaderboard.
     */

    @Override
    public String[] getLeaderboard() {
        return DriverManagers.menuLeaderboard();
    }

    /**
     * Submits points for a player.
     * <p>
     * This method submits points for the player with the specified username.
     * </p>
     *
     * @param username The username of the player.
     * @param pts      The points to be submitted.
     */

    @Override
    public void submitPts(String username, int pts) {
    }

    /**
     * Deletes a user from the system.
     * <p>
     * This method deletes the user with the specified username from the system.
     * </p>
     *
     * @param username The username of the user to be deleted.
     * @return {@code true} if the user is successfully deleted, {@code false} otherwise.
     */

    @Override
    public boolean deleteUser(String username) {
        return false;
    }

    /**
     * Creates a session for a user.
     * <p>
     * This method creates a session for the specified username.
     * If a session for the username does not already exist,
     * it adds the username to the active sessions and returns {@code true}.
     * If a session for the username already exists, it returns {@code false}.
     * </p>
     *
     * @param username The username for which to create a session.
     * @return {@code true} if the session is successfully created, {@code false} if the session already exists.
     */

    public static synchronized boolean createSession(String username) {
        if (!activeSessions.containsKey(username)) {
            activeSessions.put(username, true);
            return true;
        }
        return false;
    }

    /**
     * Retrieves the number of points per word based on its length.
     * <p>
     * This method calculates the number of points earned for a word based on its length.
     * Words of length 4 earn 100 points, words of length 5 earn 200 points, and so on.
     * </p>
     *
     * @param words The word for which to calculate the points.
     * @return The number of points earned for the word.
     */

    @Override
    public int pointsPerWord(String words) {
        int characterCount = words.length();
        if (characterCount == 4) {
            return 100;
        } else if (characterCount == 5) {
            return 200;
        } else if (characterCount == 6) {
            return 300;
        } else if (characterCount == 7) {
            return 400;
        } else if (characterCount == 8) {
            return 500;
        } else {
            return 0;
        }
    }

    /**
     * Logs out a user from the system.
     * <p>
     * This method logs out the specified user from the system by closing their session.
     * It sets the user's status as offline.
     * </p>
     *
     * @param username The username of the user to log out.
     * @return {@code true} if the user is successfully logged out, {@code false} otherwise.
     * @throws failedLogOutException If the logout process fails.
     */

    @Override
    public boolean logout(String username) throws failedLogOutException {
        boolean sessionClosed = closeSession(username);
        if (sessionClosed) {
            DriverManagers.setOffline(username);
            return true;
        } else
            throw new failedLogOutException("Unsuccessful log out");
    }

    /**
     * Closes a multiplayer game room.
     * <p>
     * This method closes the specified multiplayer game room.
     * </p>
     *
     * @param roomID The ID of the room to close.
     * @throws roomDoesntExistException If the specified room doesn't exist.
     */

    @Override
    public void closeRoom(int roomID) throws roomDoesntExistException {
        DriverManagers.closeRoom(roomID);
    }

    /**
     * Generates a set of characters for the game.
     * <p>
     * This method generates a set of characters for the game, consisting of consonants and vowels.
     * </p>
     *
     * @return An array of strings representing the generated characters.
     */

    public static synchronized boolean validateSession(String username) {
        return activeSessions.containsKey(username) && activeSessions.get(username);
    }

    /**
     * Closes a session for a user.
     * <p>
     * This method closes the session for the specified username.
     * If a session for the username exists, it removes the username from the active sessions and returns {@code true}.
     * If no session for the username exists, it returns {@code false}.
     * </p>
     *
     * @param username The username for which to close the session.
     * @return {@code true} if the session is successfully closed, {@code false} if no session exists for the username.
     */

    public synchronized boolean closeSession(String username) {
        if (activeSessions.containsKey(username)) {
            activeSessions.remove(username);
            return true;
        }
        return false;
    }

    /**
     * Generates a set of characters for the game.
     * <p>
     * This method generates a set of characters for the game, consisting of consonants and vowels.
     * It creates 13 random consonants and 7 random vowels, combining them into a list of characters.
     * </p>
     *
     * @return An array of strings representing the generated characters.
     */

    public static String[] generateCharacters() {
        java.util.List<String> characters = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 13; i++) {// to generate 13 consonants
            characters.add(String.valueOf(CONSONANTS.charAt(random.nextInt(CONSONANTS.length()))));
        }
        for (int i = 0; i < 7; i++) {// to generate 7 vowels
            characters.add(String.valueOf(VOWELS.charAt(random.nextInt(VOWELS.length()))));
        }

        return characters.toArray(new String[0]);
    }

    /**
     * Starts the countdown for a game room.
     * <p>
     * This method starts the countdown for the specified game room.
     * If the countdown for the room has already been started, it prints a message and returns.
     * It initializes the countdown timer with the initial time or a default time of 10 seconds.
     * The countdown decrements every second until it reaches zero.
     * When the countdown finishes, it shuts down the scheduler for the room.
     * </p>
     *
     * @param roomID The ID of the game room for which to start the countdown.
     */

    private void startGameCountdown(int roomID) {
        synchronized (this) {
            if (roomSchedulers.containsKey(roomID)) {
                System.out.println("Countdown already started for Room " + roomID);
                return;
            }
            int initialTime = countdowns.getOrDefault(roomID, 10);
            countdowns.put(roomID, initialTime);

            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            roomSchedulers.put(roomID, scheduler);
            scheduler.scheduleAtFixedRate(() -> {
                synchronized (this) {
                    int remainingTime = countdowns.getOrDefault(roomID, 0);
                    System.out.println("Room " + roomID + " Countdown: " + remainingTime);
                    if (remainingTime == 0) {
                        System.out.println("Room " + roomID + " Countdown finished!");
                        scheduler.shutdown(); // Shutdown this countdown scheduler
                        roomSchedulers.remove(roomID);
                    }
                    countdowns.put(roomID, remainingTime - 1);
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Generates a unique ID for a game room.
     * <p>
     * This method generates a unique ID for a game room by repeatedly generating a random ID
     * until it finds one that does not already exist in the database.
     * </p>
     *
     * @return A unique ID for the game room.
     */

    private static int generateUniqueID() {
        int uniqueID;
        do {
            uniqueID = random.nextInt(10000) + 1;
        } while (!DriverManagers.checkIDifExists(uniqueID));
        return uniqueID;
    }

    /**
     * Retrieves the high score of a player.
     * <p>
     * This method retrieves the high score of the player with the specified username.
     * If the player has no high score or if the high score is lower than the current game score,
     * it returns the current game score. Otherwise, it returns the player's high score.
     * </p>
     *
     * @param username The username of the player.
     * @return The high score of the player.
     */

    @Override
    public int getHighScore(String username) {
        Integer highScore = DriverManagers.getHighScore(username);

        if (highScore == null || highScore.equals("Null")) {
            return 0;
        }

        if (highScore < gameScore) {
            System.out.println("high score: " + highScore);
            return gameScore;
        } else if (highScore > gameScore) {
            System.out.println("high score: " + highScore);
            this.gameScore = highScore;
            return highScore;
        }

        return gameScore;
    }

    /**
     * Sets the high score of a player.
     * <p>
     * This method sets the high score of the player with the specified username.
     * If the provided score is higher than the current high score, it updates the high score
     * and stores it in the database. It also updates the current game score to the new high score.
     * </p>
     *
     * @param username The username of the player.
     * @param score    The high score to set.
     * @return The set high score.
     */

    public int setHighScore(String username, int score) {

        String highScoreStr = String.valueOf(DriverManagers.getHighScore(username));

        int highScore = 0;

        if (highScoreStr != null && !highScoreStr.equalsIgnoreCase("Null")) {
            try {
                highScore = Integer.parseInt(highScoreStr);
            } catch (NumberFormatException e) {
                System.err.println("Invalid high score format for user " + username + ": " + highScoreStr);
            }
        }

        System.out.println("sc test " + score);
        System.out.println("hs test " + highScore);

        if (score > highScore) {
            DriverManagers.storeHighScoreSinglePlayer(username, score);
            gameScore = score;
        }

        return score;
    }

    /**
     * Sets the score of a player for a round in a game room.
     * <p>
     * This method stores the score of a player for a specific round in a game room.
     * It keeps track of the current round count for the player and updates the round score accordingly.
     * </p>
     *
     * @param username The username of the player.
     * @param score    The score of the player for the round.
     * @param roomID   The ID of the game room.
     */

    public synchronized void setRoundScore(String username, int score, int roomID) {
        String roomKey = String.valueOf(roomID);

        // Get the round count map for this room
        Map<String, Integer> roomRoundCountMap = roundCountMap.computeIfAbsent(roomKey, k -> new HashMap<>());

        // Get the current round count for this player in this room
        Integer currentRoundCount = roomRoundCountMap.get(username);

        // Increment the round count if it exists, otherwise set it to 1
        if (currentRoundCount == null) {
            currentRoundCount = 1;  // Set round count to 1 for the first round
        } else {
            currentRoundCount++;    // Increment round count for subsequent rounds
        }

        // Debugging statement to print current round count for the user
        System.out.println("Current round count for " + username + " in room " + roomID + ": " + currentRoundCount);

        // Store the score based on the current round count
        DriverManagers.storeRoundScore(username, score, currentRoundCount, roomID);

        // Debugging statement to print the round number being stored
        System.out.println("Score stored in round " + currentRoundCount + " for " + username + " in room " + roomID);

        // Update the round count map for this player in this room
        roomRoundCountMap.put(username, currentRoundCount);
    }

    /**
     * Sets the total score of a player in a multiplayer game room.
     * <p>
     * This method stores the total score of a player in a multiplayer game room.
     * It updates the player's total score in the database for the specified room.
     * </p>
     *
     * @param username The username of the player.
     * @param score    The total score of the player.
     * @param roomID   The ID of the game room.
     * @return The total score of the player.
     */

    @Override
    public int setTotalScore(String username, int score, int roomID) {
        DriverManagers.storeScoreMultiPlayer(username, score, roomID);
        return score;
    }

    /**
     * Retrieves the time set for the game.
     * <p>
     * This method retrieves the time set for the game from the admin settings.
     * </p>
     *
     * @return The time set for the game in seconds.
     */

    @Override
    public int setTime() {
        int secs = AdminGUI.getSecs();
        return secs;
    }

    /**
     * Retrieves the current game leaderboards for a specific game room.
     * <p>
     * This method retrieves the current game leaderboards for the specified game room.
     * It returns an array containing the usernames and scores of players in descending order of score.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players in descending order of score.
     */

    @Override
    public String[] getCurrentGameLeaderboards(int roomID) {
        return DriverManagers.getCurrentGameLeaderboard(roomID);
    }

    /**
     * Retrieves the scores of players for the first round in a game room.
     * <p>
     * This method retrieves the scores of players for the first round in the specified game room.
     * It returns an array containing the usernames and scores of players for the first round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the first round.
     */

    @Override
    public String[] getRound1Score(int roomID) {
        return DriverManagers.getRound1Score(roomID);
    }

    /**
     * Retrieves the scores of players for the second round in a game room.
     * <p>
     * This method retrieves the scores of players for the second round in the specified game room.
     * It returns an array containing the usernames and scores of players for the second round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the second round.
     */

    @Override
    public String[] getRound2Score(int roomID) {
        return DriverManagers.getRound2Score(roomID);
    }

    /**
     * Retrieves the scores of players for the third round in a game room.
     * <p>
     * This method retrieves the scores of players for the third round in the specified game room.
     * It returns an array containing the usernames and scores of players for the third round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the third round.
     */

    @Override
    public String[] getRound3Score(int roomID) {
        return DriverManagers.getRound3Score(roomID);
    }

    /**
     * Retrieves the scores of players for the fourth round in a game room.
     * <p>
     * This method retrieves the scores of players for the fourth round in the specified game room.
     * It returns an array containing the usernames and scores of players for the fourth round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the fourth round.
     */

    @Override
    public String[] getRound4Score(int roomID) {
        return DriverManagers.getRound4Score(roomID);
    }

    /**
     * Retrieves the scores of players for the fifth round in a game room.
     * <p>
     * This method retrieves the scores of players for the fifth round in the specified game room.
     * It returns an array containing the usernames and scores of players for the fifth round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the fifth round.
     */

    @Override
    public String[] getRound5Score(int roomID) {
        return DriverManagers.getRound5Score(roomID);
    }

    /**
     * Retrieves the scores of players for the sixth round in a game room.
     * <p>
     * This method retrieves the scores of players for the sixth round in the specified game room.
     * It returns an array containing the usernames and scores of players for the sixth round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the sixth round.
     */

    @Override
    public String[] getRound6Score(int roomID) {
        return DriverManagers.getRound6Score(roomID);
    }

    /**
     * Retrieves the scores of players for the seventh round in a game room.
     * <p>
     * This method retrieves the scores of players for the seventh round in the specified game room.
     * It returns an array containing the usernames and scores of players for the seventh round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the seventh round.
     */

    @Override
    public String[] getRound7Score(int roomID) {
        return DriverManagers.getRound7Score(roomID);
    }

    /**
     * Retrieves the scores of players for the eight round in a game room.
     * <p>
     * This method retrieves the scores of players for the eight round in the specified game room.
     * It returns an array containing the usernames and scores of players for the eight round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the eight round.
     */

    @Override
    public String[] getRound8Score(int roomID) {
        return DriverManagers.getRound8Score(roomID);
    }

    /**
     * Retrieves the scores of players for the ninth round in a game room.
     * <p>
     * This method retrieves the scores of players for the ninth round in the specified game room.
     * It returns an array containing the usernames and scores of players for the ninth round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the ninth round.
     */

    @Override
    public String[] getRound9Score(int roomID) {
        return DriverManagers.getRound9Score(roomID);
    }

    /**
     * Retrieves the scores of players for the tenth round in a game room.
     * <p>
     * This method retrieves the scores of players for the tenth round in the specified game room.
     * It returns an array containing the usernames and scores of players for the tenth round.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return An array containing the usernames and scores of players for the tenth round.
     */

    @Override
    public String[] getRound10Score(int roomID) {
        return DriverManagers.getRound10Score(roomID);
    }

    /**
     * Checks if a player has joined a multiplayer game room.
     * <p>
     * This method checks if any players have joined the specified multiplayer game room.
     * It returns true if at least one player has joined, otherwise false.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return True if at least one player has joined the game room, otherwise false.
     */

    @Override
    public boolean checkPlayerJoined(int roomID) {
        return DriverManagers.checkPlayers(roomID);
    }

    /**
     * Signals that a player has completed their turn in a game room.
     * <p>
     * This method marks the game status as done for the specified game room,
     * indicating that a player has completed their turn in the game.
     * </p>
     *
     * @param roomID The ID of the game room.
     */

    private Map<Integer, Boolean> gameStatus = new HashMap<>();
    @Override
    public synchronized void sendDone(int roomID) {
        gameStatus.put(roomID, true);
        System.out.println("Received done signal for room ID: " + roomID);
    }

    /**
     * Checks if the game in a specific room is done.
     * <p>
     * This method checks if the game in the specified game room is done.
     * It returns true if the game is done, otherwise false.
     * </p>
     *
     * @param roomID The ID of the game room.
     * @return True if the game is done, otherwise false.
     */

    @Override
    public synchronized boolean checkGameDone(int roomID) {
        Boolean status = gameStatus.get(roomID);
        if (status == null) {
            System.out.println("No status found for room ID: " + roomID);
            return false;
        }
        return status;
    }

    /**
     * Checks if a word is valid based on a dictionary file.
     * <p>
     * This method reads a dictionary file containing valid words.
     * It checks if the provided word exists in the dictionary file and returns true if it does,
     * otherwise false.
     * </p>
     *
     * @param word The word to check for validity.
     * @return True if the word is valid, otherwise false.
     */

    public boolean checkWordIfValid(String word) {
        try (BufferedReader br = new BufferedReader(new FileReader("FinalProject/lib/words.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equalsIgnoreCase(word)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

}