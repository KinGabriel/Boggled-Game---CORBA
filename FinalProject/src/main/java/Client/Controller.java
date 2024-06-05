package Client;

import boggledApp.*;
import View.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The Controller class acts as the main controller for the client-side application.
 * It manages interactions between various views and the client application logic.
 * This class handles user authentication, navigation between different views,
 * game management, multiplayer functionality, and other client-side operations.
 */

public class Controller {
    private static int avatar;
    private static Controller instance;
    private static clientApp clientApp;
    private Front login;
    private Register register;
    private Homepage homepage;
    private Loading loading;
    private static Game game;
    private Account account;
    private Leaderboards leaderboards;
    private static SinglePlayer singlePlayer;
    private static LobbyMultiplayer lobbyMultiplayer;
    private static Result result;
    private static LoaderMultiplayer loaderMultiplayer;
    private static WaitingMultiplayer waitingMultiplayer;
    private static IPAddress ipAddress;
    private static TimerMultiplayer timerMultiplayer;
    private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static final Object lock = new Object();
    private static boolean countdownActive = false;
    private static boolean isSinglePlayer;
    private int time;
    private static Object gameLeads;
    private List<String> topPlayerUsername = new ArrayList<>();
    private static int roomID;
    private static ArrayList<Integer> wordPointsList = new ArrayList<>();
    private static ArrayList<String> submittedWordsList = new ArrayList<>();
    private static ArrayList<String> submittedWordsListPerRound = new ArrayList<>();
    private static ArrayList<String> roundWinners = new ArrayList<>();

    /**
     * Constructs a new Controller instance.
     * Initializes various views and the client application instance.
     * This constructor sets up the initial state of the client-side application.
     */

    public Controller() {
        this.clientApp = new clientApp();
        this.login = new Front();
        this.register = new Register();
        this.homepage = new Homepage();
        this.loading = new Loading();
        this.game = new Game();
        this.account = new Account();
        this.leaderboards = new Leaderboards();
        this.singlePlayer = new SinglePlayer();
        this.lobbyMultiplayer = new LobbyMultiplayer();
        this.result = new Result();
        this.loaderMultiplayer = new LoaderMultiplayer();
        this.waitingMultiplayer = new WaitingMultiplayer();
        this.ipAddress = new IPAddress();
        this.timerMultiplayer = new TimerMultiplayer();
    }

    /**
     * Gets the singleton instance of the Controller class.
     * If the instance does not exist, it creates a new instance.
     * This method ensures that there is only one instance of the Controller class
     * throughout the lifecycle of the client-side application.
     *
     * @return The singleton instance of the Controller class.
     */

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Takes an IP address as input and runs the client application with the specified IP.
     *
     * @param ip The IP address to be used for running the client application.
     * @return The result of running the client application with the specified IP address.
     */

    public static String inputIP(String ip){
        return clientApp.run(ip);
    }

    /**
     * Logs in a user with the provided username and password.
     * Displays appropriate messages based on the outcome of the login attempt.
     *
     * @param username The username of the user attempting to log in.
     * @param password The password of the user attempting to log in.
     */

    public void login(String username, String password) {
        try {
            boolean validateLogIn = false;
            try {
                validateLogIn = clientApp.login(username, password);
            } catch (InvalidName e) {
                throw new RuntimeException(e);
            } catch (AdapterInactive e) {
                throw new RuntimeException(e);
            } catch (WrongPolicy e) {
                throw new RuntimeException(e);
            } catch (ServantNotActive e) {
                throw new RuntimeException(e);
            }
            if (validateLogIn) {
                JOptionPane.showMessageDialog(null, "Successful log in!", "Success", JOptionPane.INFORMATION_MESSAGE);
                this.avatar = clientApp.getPlayerAvatar();
                login.dispose();
                showHomepage();
            } else {
                JOptionPane.showMessageDialog(null, "Unexpected error during the login!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (userAlreadyLogInException e) {
            JOptionPane.showMessageDialog(null, "Account is already online!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (invalidCredentialsException e) {
            JOptionPane.showMessageDialog(null, "Invalid username or password!", "Failed", JOptionPane.ERROR_MESSAGE);
        } catch (bannedAccountException e) {
            JOptionPane.showMessageDialog(null, "Your account is banned!", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registers a new user with the provided username, password, and retype password.
     * Displays appropriate messages based on the outcome of the registration attempt.
     *
     * @param username        The username of the user to be registered.
     * @param password        The password of the user to be registered.
     * @param retypePassword  The retype password for confirming the password during registration.
     */

    public void register(String username, String password, String retypePassword) {
        username.trim();
        password.trim();
        retypePassword.trim();
        if (username.isEmpty() || password.isEmpty() || retypePassword.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter all fields", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(null, "Password should be 6 character long!", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(retypePassword)) {
            JOptionPane.showMessageDialog(null, "Password doesn't match! please try again...", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            boolean response = clientApp.register(username, password);
            if (response) {
                JOptionPane.showMessageDialog(null, "Successful registration", "Success", JOptionPane.INFORMATION_MESSAGE);
                register.dispose();
                login.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(null, "Unexpected error during the registration!", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            }

        } catch (usernameAlreadyExistsException e) {
            JOptionPane.showMessageDialog(null, "Username already taken! please try again", "Invalid registration", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Registers a new user with the provided username, password, and retype password.
     * Displays appropriate messages based on the outcome of the registration attempt.
     *
     * @param username       The username of the user to be registered.
     * @param password       The password of the user to be registered.
     * @param retypePassword The retype password for confirming the password during registration.
     */

    public void updateProfile(String username, String password, String retypePassword) {
        username.trim();
        password.trim();
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(null, "Password should be 6 character long!", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!password.equals(retypePassword)) {
            JOptionPane.showMessageDialog(null, "Password doesn't match! please try again...", "Invalid registration", JOptionPane.ERROR_MESSAGE);
            return;
        }
        boolean response = clientApp.updateProfile(username, password);
        if (response) {
            JOptionPane.showMessageDialog(null, "Successful update profile", "Success", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(null, "Unexpected error during the update profile!", "Invalid update profile", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Updates the avatar of the user with the provided avatar ID.
     * Displays appropriate messages based on the outcome of the avatar update attempt.
     *
     * @param avatar The new avatar ID for the user.
     */

    public void updateAvatar(int avatar) {
        boolean response = clientApp.updatePlayerAvatar(avatar);
        if (response) {
            JOptionPane.showMessageDialog(null, "Successful update avatar", "Success", JOptionPane.INFORMATION_MESSAGE);
            this.avatar = avatar;
            account.dispose();
            showAccount();
            return;
        }
        JOptionPane.showMessageDialog(null, "Unexpected error during the update profile!", "Invalid update profile", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Checks if a word has already been submitted.
     *
     * @param word The word to check for repetition.
     * @return true if the word is already in the list of submitted words, otherwise false.
     */

    public boolean isWordRepeated(String word) {
        return submittedWordsList.contains(word);
    }

    /**
     * Handles the submission of words by the player during the game.
     * Checks if the submitted word is valid and calculates points.
     * Updates the game interface accordingly.
     *
     * @param words The word submitted by the player.
     */

    public void handleSubmittedWords(String words) {
        boolean isValidWord = false;
        try {
            isValidWord = clientApp.checkWord(words,roomID);
        } catch (invalidWordException e) {
            // game.displayWordPoints("Invalid word!",0);
        }
        if (isValidWord) {
            if (!isWordRepeated(words)) { // Check if the word is not repeated
                submittedWordsList.add(words); // Add the word to the list of submitted words
                submittedWordsListPerRound.add(words);
                game.markUsedLetters(words);
                int points = clientApp.wordPoints(words); // Calculate points for the word
                wordPointsList.add(points); // Store the word's points in the list
                game.displayWordPoints(words, points);
            } else {
                // String message = "Word '" + words + "' has already been submitted!";
                // game.displayWordPoints(message, 0); // Display the message in the wordsMade text area
            }
        } else {
            int points = clientApp.wordPoints(words); // Calculate points for the word
            game.displayWordPoints(words, points);
        }
    }

    /**
     * Computes the total score based on the points accumulated from submitted words.
     *
     * @return The total score of the player.
     */

    public static int computeTotalScore() {
        int totalScore = 0;
        for (Integer points : wordPointsList) {
            totalScore += points;
        }
        return totalScore;
    }

    /**
     * Handles actions to be taken upon timer completion, such as updating the score display.
     */

    public static void handleTimerCompletion() {
        int totalScore = computeTotalScore();
        result.Score1.setText(String.valueOf(totalScore));
    }


    /**
     * Initiates a single-player game session, resetting game-related data and starting the timer.
     * Retrieves the game letters from the server and updates the game interface.
     */

    public void createGameSinglePlayer() {
        isSinglePlayer = true;
        submittedWordsList.clear();
        submittedWordsListPerRound.clear();
        wordPointsList.clear();
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.resetAllWordsMade();
        String[] letters = clientApp.createGameSinglePlayer();
        game.updateLetterButtons(letters);
        game.updateButtonBackgrounds();
        showGame();
        game.username.setText(clientApp.getUserDetails());
        setTime();
        game.startTimer();
    }

    /**
     * Logs out the current user from the system.
     * Closes the homepage window upon successful logout.
     *
     * @throws IOException If an I/O error occurs while communicating with the server.
     */

    public void logout() throws IOException {
        try {
            boolean response = clientApp.logout();
            if (response) {
                JOptionPane.showMessageDialog(null, "Successfully logged out!");
                homepage.dispose();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error communicating with the server.", "Log out Failed", JOptionPane.ERROR_MESSAGE);
        } catch (failedLogOutException e) {
            JOptionPane.showMessageDialog(null, "Unsuccessful log out!");
        }
    }

    /**
     * Retrieves the list of available rooms from the server and updates the lobby interface.
     */

    public static void showRooms() {
        String[] showRooms = clientApp.listOfRooms();
        DefaultTableModel model = (DefaultTableModel) lobbyMultiplayer.roomsTable.getModel();
        model.setRowCount(0);
        for (String roomData : showRooms) {
            String[] roomInfo = roomData.split(" \\| ");
            Object[] rowData = {roomInfo[0], roomInfo[1], roomInfo[2]}; // roomID, count, host
            model.addRow(rowData);
        }
    }

    /**
     * Sends the selected room ID to the server to join the corresponding multiplayer game.
     * Initiates the game setup and countdown process.
     *
     * @param roomID The ID of the room to join.
     */

    public void sendRoomID(int roomID){
        game.jLabel5.setVisible(false);
        game.jLabel6.setVisible(false);
        game.jLabel7.setVisible(false);
        game.jLabel8.setVisible(false);
        roundWinners.clear();
        topPlayerUsername.clear();
        wordPointsList.clear();
        submittedWordsList.clear();
        submittedWordsListPerRound.clear();
        try {
            int secs = clientApp.sendRoom(roomID);
//            if(secs != 0){
            showStartGame();
            startCountdown(secs,roomID);
            timerMultiplayer.jLabel1.setText("ROOM: " + roomID);
//            }

        } catch (roomDoesntExistException e) {
            JOptionPane.showMessageDialog(null, "Room doesn't exist", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Sends the selected room ID to the server to join the corresponding multiplayer game.
     * Initiates the game setup and countdown process.
     *
     * @param roomID The ID of the room to join.
     */

    public void leaveRoom(int roomID){
        clientApp.leaveRoom(roomID);
        stopCountdown();
        timerMultiplayer.dispose();
        showMultiPlayerLobby();
    }

    /**
     * Creates a multiplayer room and initializes the game setup.
     * Clears previous game data and prepares for a new multiplayer game.
     * Shows the game interface with the countdown timer.
     */

    public void createRoom(){
        game.jLabel5.setVisible(false);
        game.jLabel6.setVisible(false);
        game.jLabel7.setVisible(false);
        game.jLabel8.setVisible(false);
        roundWinners.clear();
        topPlayerUsername.clear();
        isSinglePlayer = false;
        submittedWordsList.clear();
        submittedWordsListPerRound.clear();
        wordPointsList.clear();
        int[] result =clientApp.createRoom();
        int roomID = result[0];
        int remainingTime = result[1];
        int countDown = remainingTime;
        showStartGame();
        timerMultiplayer.jLabel1.setText("ROOM: " + roomID);
        startCountdown(countDown,roomID);
        // JOptionPane.showMessageDialog(null, "You have created a room!", "Create game", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Displays the login window.
     * Closes other windows if open.
     */

    public void showLogin(){
        ipAddress.dispose();
        register.dispose();
        login.setLocationRelativeTo(null);
        login.setResizable(false);
        login.setVisible(true);
    }

    /**
     * Displays the register window.
     * Closes other windows if open.
     */

    public void showRegister(){
        login.dispose();
        register.setLocationRelativeTo(null);
        register.setResizable(false);
        register.setVisible(true);
    }

    /**
     * Displays the homepage window.
     * Closes other windows if open.
     * Populates the homepage with user details and avatar.
     */

    public void showHomepage(){
        loading.dispose();
        result.dispose();
        account.dispose();
        singlePlayer.dispose();
        leaderboards.dispose();
        homepage.nameInput.setText(clientApp.getUserDetails());
        if (avatar == 1) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/captainAmerica.png")));
        }else if(avatar == 2) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spiderman.png")));
        }else if(avatar == 3) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/grinch.png")));
        }else if(avatar == 4) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stitch.png")));
        }else if(avatar == 5) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bubbles.png")));
        }else if(avatar == 6) {
            homepage.iconInput.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kuromi.png")));
        }
        homepage.setLocationRelativeTo(null);
        homepage.setResizable(false);
        homepage.setVisible(true);
    }

    /**
     * Displays the loading window.
     * Closes other windows if open.
     */

    public void showLoading(){
        homepage.dispose();
        loading.setLocationRelativeTo(null);
        loading.setResizable(false);
        loading.setVisible(true);
    }

    /**
     * Displays the game interface.
     * Closes the loading and single player windows if open.
     * Sets the username and avatar on the game window based on the client's details.
     */

    public void showGame(){
        loading.dispose();
        singlePlayer.dispose();
        game.username.setText(clientApp.getUserDetails());
        if (avatar == 1) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/captainAmerica.png")));
        }else if(avatar == 2) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spiderman.png")));
        }else if(avatar == 3) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/grinch.png")));
        }else if(avatar == 4) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stitch.png")));
        }else if(avatar == 5) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bubbles.png")));
        }else if(avatar == 6) {
            game.avatar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kuromi.png")));
        }
        game.setLocationRelativeTo(null);
        game.setResizable(false);
        game.setVisible(true);
    }

    /**
     * Displays the account window.
     * Closes the homepage window if open.
     * Sets the username and avatar on the account window based on the client's details.
     */

    public void showAccount(){
        homepage.dispose();
        account.usernameInput.setText( clientApp.getUserDetails());
        if (avatar == 1) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/captainAmerica.png")));
        }else if(avatar == 2) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/spiderman.png")));
        }else if(avatar == 3) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/grinch.png")));
        }else if(avatar == 4) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/stitch.png")));
        }else if(avatar == 5) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/bubbles.png")));
        }else if(avatar == 6) {
            account.jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/kuromi.png")));
        }
        account.setLocationRelativeTo(null);
        account.setResizable(false);
        account.setVisible(true);
    }

    /**
     * Displays the leaderboards window.
     * Closes the homepage window if open.
     */

    public void showLeaderboards(){
        displayLeaderBoards();
        homepage.dispose();
        leaderboards.setLocationRelativeTo(null);
        leaderboards.setResizable(false);
        leaderboards.setVisible(true);
    }

    /**
     * Shows the game summary window for single-player mode.
     * Closes the game window.
     * Sets the user's details and avatar in the summary window.
     * Displays the user's word count and high score.
     */

    public static void showGameSummarySinglePlayer() {
        game.dispose();
        singlePlayer.user1.setText(clientApp.getUserDetails());
        setAvatarIcon(singlePlayer.avatarWinner, avatar);
        int wordCount = countValidWords();
        singlePlayer.wordsTF.setText(String.valueOf(wordCount));
        setHighScore();
        singlePlayer.HighscoreTF.setText(String.valueOf(getHighScore(clientApp.getUserDetails())));
        singlePlayer.textAreaofWords1.setText(game.getGameSummary());
        singlePlayer.setLocationRelativeTo(null);
        singlePlayer.setResizable(false);
        singlePlayer.setVisible(true);
    }

    /**
     * Shows the game summary window for multi-player mode.
     * Closes the game window.
     * Populates the summary window with information about game leads.
     * Displays usernames, avatars, and word counts of top players.
     */

    public static void showGameSummaryMultiPlayer() {
        game.dispose();

        Object gameLeads = getGameLeads(); // Assuming this method retrieves the game leads
        if (gameLeads instanceof String) {
            String gameLeadsString = (String) gameLeads;
            String[] leads = gameLeadsString.split(",");

            for (int i = 0; i < Math.min(7, leads.length); i++) {
                String placeInfo = leads[i].trim();
                String[] placeData = placeInfo.split("\\|");
                String username = placeData[0].substring(placeData[0].indexOf(":") + 1).trim();
                int totalScore = Integer.parseInt(placeData[1].substring(placeData[1].indexOf(":") + 1).trim());
                int avatarID = Integer.parseInt(placeData[2].substring(placeData[2].indexOf(":") + 1).trim());

                switch (i) {
                    case 0:
                        result.user1.setText(username);
                        setAvatarIcon(result.avatarWinner, avatarID);
                        result.WordCount1.setText(String.valueOf(totalScore));
                        result.textAreaofWords1.setText("1st Place!!!");
                        break;
                    case 1:
                        result.user2.setText(username);
                        setAvatarIcon(result.avatar2nd, avatarID);
                        result.WordCount2.setText(String.valueOf(totalScore));
                        result.textAreaofWords2.setText("2nd Place!!!");
                        break;
                    case 2:
                        result.user3.setText(username);
                        setAvatarIcon(result.avatar3rd, avatarID);
                        result.WordCount3.setText(String.valueOf(totalScore));
                        result.textAreaofWords3.setText("3rd Place!!!");
                        break;
                    case 3:
                        result.user4.setText(username);
                        setAvatarIcon(result.avatar4th, avatarID);
                        result.WordCount4.setText(String.valueOf(totalScore));
                        result.textAreaofWords4.setText("4th Place!!!");
                        break;
                    case 4:
                        result.user5.setText(username);
                        setAvatarIcon(result.avatar5th, avatarID);
                        result.WordCount5.setText(String.valueOf(totalScore));
                        result.textAreaofWords5.setText("5th Place!!!");
                        break;
                    case 5:
                        result.user6.setText(username);
                        setAvatarIcon(result.avatar6th, avatarID);
                        result.WordCount6.setText(String.valueOf(totalScore));
                        result.textAreaofWords6.setText("6th Place!!!");
                        break;
                    case 6:
                        result.user7.setText(username);
                        setAvatarIcon(result.avatar7th, avatarID);
                        result.WordCount7.setText(String.valueOf(totalScore));
                        result.textAreaofWords7.setText("7th Place!!!");
                        break;
                    default:
                        break;
                }
            }
        }
        result.setLocationRelativeTo(null);
        result.setResizable(false);
        result.setVisible(true);
    }

    /**
     * Sets the avatar icon for a given JLabel based on the avatar ID.
     *
     * @param avatarLabel The JLabel to set the avatar icon for.
     * @param avatar      The avatar ID.
     */

    private static void setAvatarIcon(javax.swing.JLabel avatarLabel, int avatar) {
        switch (avatar) {
            case 1:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/captainAmerica.png")));
                break;
            case 2:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/spiderman.png")));
                break;
            case 3:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/grinch.png")));
                break;
            case 4:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/stitch.png")));
                break;
            case 5:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/bubbles.png")));
                break;
            case 6:
                avatarLabel.setIcon(new javax.swing.ImageIcon(Controller.class.getResource("/kuromi.png")));
                break;
        }
    }

    /**
     * Shows the game summary window, determining whether to show single-player or multi-player summary.
     */

    public static void showGameSummary() {
        if (isSinglePlayer) {
            showGameSummarySinglePlayer();
        } else {
            showGameSummaryMultiPlayer();
        }
    }

    /**
     * Shows the multiplayer lobby window.
     * Closes the homepage window if open.
     * Displays the available rooms.
     */

    public void showMultiPlayerLobby(){
        showRooms();
        homepage.dispose();
        lobbyMultiplayer.setLocationRelativeTo(null);
        lobbyMultiplayer.setResizable(false);
        lobbyMultiplayer.setVisible(true);
    }

    /**
     * Shows the lobby loader window and disposes of the multiplayer lobby window.
     */

    public static void showLobbyLoader(){
        lobbyMultiplayer.dispose();
        loaderMultiplayer.setLocationRelativeTo(null);
        loaderMultiplayer.setResizable(false);
        loaderMultiplayer.setVisible(true);
    }

    /**
     * Shows the lobby loader window and disposes of the multiplayer lobby window.
     */

    public static void showWaitingLobbyLoader(){
        lobbyMultiplayer.dispose();
        waitingMultiplayer.setLocationRelativeTo(null);
        waitingMultiplayer.setResizable(false);
        waitingMultiplayer.setVisible(true);
    }

    /**
     * Shows the IP address window.
     */

    public static void showIP(){
        ipAddress.setLocationRelativeTo(null);
        ipAddress.setResizable(false);
        ipAddress.setVisible(true);
    }

    /**
     * Shows the start game window and disposes of the multiplayer lobby window.
     */

    public static void showStartGame(){
        lobbyMultiplayer.dispose();
        timerMultiplayer.setLocationRelativeTo(null);
        timerMultiplayer.setResizable(false);
        timerMultiplayer.setVisible(true);
    }

    /**
     * Counts the number of valid words in the submitted words list.
     *
     * @return The count of valid words.
     */

    public static int countValidWords() {
        int count = 0;
        for (String word : submittedWordsList) {
            boolean isValid = false;
            try {
                isValid = clientApp.checkWord(word,roomID);
            } catch (invalidWordException e) {
                throw new RuntimeException(e);////
            }
            if (isValid) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts the number of valid words per round in the submitted words list.
     *
     * @return The count of valid words per round.
     */

    public static int countValidWordsPerRound() {
        int count = 0;
        for (String word : submittedWordsListPerRound) {
            boolean isValid = false;
            try {
                isValid = clientApp.checkWord(word,roomID);
            } catch (invalidWordException e) {
                throw new RuntimeException(e);
            }
            if (isValid) {
                count++;
            }
        }
        return count;
    }

    /**
     * Starts round 1 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound1(int roomID){
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round1 = new String[20];
        String firstWord = letters[0];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round1.length); i++) {
                round1[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        if(time == 0){
            this.time = 60;
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round1);
        game.updateButtonBackgrounds();
        showGame();
        game.username.setText(clientApp.getUserDetails());
        setTime();
        game.startTimer();
        Timer saveRound1ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 1 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 1");
            }
        });
        saveRound1ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound1ScoreTimer.start();

        Timer delayTimer = new Timer(time * 1000 + 1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,1);
            }
        });
        delayTimer.setRepeats(false); // Set to execute only once
        delayTimer.start();
        startRound2Timer(time, roomID);
    }

    /**
     * Starts round 2 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound2(int roomID){
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round2 = new String[20];
        String firstWord = letters[1];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round2.length); i++) {
                round2[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round2);
        game.updateButtonBackgrounds();
        showGame();
        game.username.setText(clientApp.getUserDetails());
        setTime();
        game.startTimer();
        Timer saveRound2ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 2 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 2");
            }
        });
        saveRound2ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound2ScoreTimer.start();

        Timer delayTimer = new Timer(time * 1000 + 1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,2);
            }
        });
        delayTimer.setRepeats(false); // Set to execute only once
        delayTimer.start();
        startRound3Timer(time, roomID);
    }

    /**
     * Starts round 3 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound3(int roomID){
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round3 = new String[20];
        String firstWord = letters[2];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round3.length); i++) {
                round3[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round3);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();
        Timer saveRound3ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 3 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 3");
            }
        });
        saveRound3ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound3ScoreTimer.start();
        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,3);
                boolean isJLabelVisible = game.jLabel7.isVisible();
                System.out.println("jLabel7 visibility after compareRound3Scores: " + isJLabelVisible);

                if (isJLabelVisible) {
                    System.out.println("Showing summary because jLabel7 is visible");
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    System.out.println("checkGameDone response: " + response);
                    if(response){
                        showSummary(0, roomID);
                        System.out.println("Showing summary because checkGameDone returned true");
                    }else {
                        startRound4Timer(0, roomID); // Trigger the next round
                        result.dispose();
                        System.out.println("Starting round 4 because checkGameDone returned false");
                    }
                }
            }
        });
        summaryTimer.setRepeats(false); // Set to execute only once
        summaryTimer.start();
    }

    /**
     * Starts round 4 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound4(int roomID){
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round4 = new String[20];
        String firstWord = letters[3];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round4.length); i++) {
                round4[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round4);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();
        Timer saveRound4ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 4 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 4");
            }
        });
        saveRound4ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound4ScoreTimer.start();
        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,4);
                boolean isJLabelVisible = game.jLabel7.isVisible();
                System.out.println("jLabel7 visibility after compareRound4Scores: " + isJLabelVisible);

                if (isJLabelVisible) {
                    System.out.println("Showing summary because jLabel7 is visible");
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    System.out.println("checkGameDone response: " + response);
                    if(response){
                        showSummary(0, roomID);
                        System.out.println("Showing summary because checkGameDone returned true");
                    }else {
                        startRound5Timer(0, roomID); // Trigger the next round
                        System.out.println("Starting round 5 because checkGameDone returned false");
                    }
                }
            }
        });
        summaryTimer.setRepeats(false); // Set to execute only once
        summaryTimer.start();
    }

    /**
     * Starts round 5 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound5(int roomID){ // not yet edited
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round5 = new String[20];
        String firstWord = letters[4];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round5.length); i++) {
                round5[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round5);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();
        Timer saveRound5ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 5 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 5");
            }
        });
        saveRound5ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound5ScoreTimer.start();
        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,5);
                if (game.jLabel7.isVisible()) {
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    if(!response){
                        startRound6Timer(0, roomID); // Trigger the next round
                    }else {
                        showSummary(0, roomID);
                    }
                }
            }
        });
        summaryTimer.setRepeats(false); // Set to execute only once
        summaryTimer.start();
    }


    /**
     * Starts round 6 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound6(int roomID){ /// new method
        String[] letters =  clientApp.multiplayerChars(roomID);
        String[] round6 = new String[20];
        String firstWord = letters[5];
        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round6.length); i++) {
                round6[i] = String.valueOf(firstWord.charAt(i));
            }
        }
        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round6);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();
        Timer saveRound6ScoreTimer = new Timer(time * 1000, new ActionListener() { // Assuming round 1 lasts for 60 seconds
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID); // Save round 6 score
                submittedWordsListPerRound.clear();//try
                System.out.println("cleared words round 6");
            }
        });
        saveRound6ScoreTimer.setRepeats(false); // Set to execute only once
        saveRound6ScoreTimer.start();
        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,6);
                if (game.jLabel7.isVisible()) {
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    if(!response){
                        startRound7Timer(0, roomID); // Trigger the next round
                    }else {
                        showSummary(0, roomID);
                    }
                }
            }
        });
        summaryTimer.setRepeats(false); // Set to execute only once
        summaryTimer.start();
    }

    /**
     * Starts round 7 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound7(int roomID) {
        String[] letters = clientApp.multiplayerChars(roomID);
        String[] round7 = new String[20];
        String firstWord = letters[6];

        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round7.length); i++) {
                round7[i] = String.valueOf(firstWord.charAt(i));
            }
        }

        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round7);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();

        Timer saveRound7ScoreTimer = new Timer(time * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID);
                submittedWordsListPerRound.clear();
                System.out.println("cleared words round 7");
            }
        });
        saveRound7ScoreTimer.setRepeats(false);
        saveRound7ScoreTimer.start();

        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,7);
                if (game.jLabel7.isVisible()) {
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    if(!response){
                        startRound8Timer(0, roomID); // Trigger the next round
                    }else {
                        showSummary(0, roomID);
                    }
                }
            }
        });
        summaryTimer.setRepeats(false);
        summaryTimer.start();
    }

    /**
     * Starts round 8 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound8(int roomID) {
        String[] letters = clientApp.multiplayerChars(roomID);
        String[] round8 = new String[20];
        String firstWord = letters[7];

        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round8.length); i++) {
                round8[i] = String.valueOf(firstWord.charAt(i));
            }
        }

        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round8);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();

        Timer saveRound8ScoreTimer = new Timer(time * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID);
                submittedWordsListPerRound.clear();
                System.out.println("cleared words round 8");
            }
        });
        saveRound8ScoreTimer.setRepeats(false);
        saveRound8ScoreTimer.start();

        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,8);
                if (game.jLabel7.isVisible()) {
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    if(!response){
                        startRound9Timer(0, roomID); // Trigger the next round
                    }else {
                        showSummary(0, roomID);
                    }
                }
            }
        });
        summaryTimer.setRepeats(false);
        summaryTimer.start();
    }

    /**
     * Starts round 9 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound9(int roomID) {
        String[] letters = clientApp.multiplayerChars(roomID);
        String[] round9 = new String[20];
        String firstWord = letters[8];

        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round9.length); i++) {
                round9[i] = String.valueOf(firstWord.charAt(i));
            }
        }

        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round9);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();

        Timer saveRound9ScoreTimer = new Timer(time * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID);
                submittedWordsListPerRound.clear();
                System.out.println("cleared words round 9");
            }
        });
        saveRound9ScoreTimer.setRepeats(false);
        saveRound9ScoreTimer.start();

        Timer summaryTimer = new Timer(time * 1000 + 2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID,9);
                if (game.jLabel7.isVisible()) {
                    showSummary(0, roomID);
                    clientApp.sendDone(roomID);
                } else {
                    boolean response =  clientApp.checkGameDone(roomID);
                    if(!response){
                        startRound10Timer(0, roomID); // Trigger the next round
                    }else {
                        showSummary(0, roomID);
                    }
                }
            }
        });
        summaryTimer.setRepeats(false);
        summaryTimer.start();
    }

    /**
     * Starts round 10 of the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public void multiplayerGameRound10(int roomID) {
        String[] letters = clientApp.multiplayerChars(roomID);
        String[] round10 = new String[20];
        String firstWord = letters[9];

        if (firstWord != null && !firstWord.isEmpty()) {
            for (int i = 0; i < Math.min(firstWord.length(), round10.length); i++) {
                round10[i] = String.valueOf(firstWord.charAt(i));
            }
        }

        homepage.dispose();
        game.dispose();
        singlePlayer.dispose();
        game.reset();
        game.updateLetterButtons(round10);
        game.updateButtonBackgrounds();
        showGame();
        setTime();
        game.username.setText(clientApp.getUserDetails());
        game.startTimer();

        Timer saveRound10ScoreTimer = new Timer(time * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setRoundScore(roomID);
                submittedWordsListPerRound.clear();
                System.out.println("cleared words round 10");
            }
        });
        saveRound10ScoreTimer.setRepeats(false);
        saveRound10ScoreTimer.start();

        Timer delayTimer = new Timer(time * 1000 + 1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                compareScores(roomID, 10);
                Timer summaryTimer = new Timer(1500, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        showSummary(0, roomID);
                    }
                });
                summaryTimer.setRepeats(false);
                summaryTimer.start();
            }
        });
        delayTimer.setRepeats(false);
        delayTimer.start();
    }

    /**
     * Starts the countdown timer for the multiplayer game.
     *
     * @param countDown The initial countdown value.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startCountdown(int countDown,int roomID) {
        synchronized (lock) {
            if (!countdownActive) {
                int[] remainingTime = {countDown};
                scheduler = Executors.newScheduledThreadPool(1);
                scheduler.scheduleAtFixedRate(() -> {
                    timerMultiplayer.timer.setText(String.valueOf(remainingTime[0]));
                    if (remainingTime[0] == 0) {
                        System.out.println("Countdown finished!");
                        stopCountdown();
                        try {
                            if (clientApp.checkPlayers(roomID)) {
                                clientApp.closeRoom(roomID);
                                this.roomID = roomID;
                                timerMultiplayer.dispose();
                                multiplayerGameRound1(roomID);
                            }else {
                                JOptionPane.showMessageDialog(null, "No other player joined the room! please try again", "Try Again", JOptionPane.WARNING_MESSAGE);
                                clientApp.closeRoom(roomID);
                                timerMultiplayer.dispose();
                                showMultiPlayerLobby();
                            }
                        } catch (roomDoesntExistException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    remainingTime[0]--;
                }, 0, 1, TimeUnit.SECONDS);
                countdownActive = true;
            }
        }
    }

    /**
     * Stops the countdown timer for the multiplayer game.
     */

    public static void stopCountdown() {
        synchronized (lock) {
            if (countdownActive && scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                scheduler = null;
                countdownActive = false;
            }
        }
    }

    /**
     * Starts the timer for the second round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound2Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound2(roomID);
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the third round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound3Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound3(roomID);
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the fourth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound4Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound4(roomID);
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the fifth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound5Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound5(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the sixth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound6Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound6(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the seventh round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound7Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound7(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the eighth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound8Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound8(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the ninth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound9Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound9(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Starts the timer for the tenth round of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void startRound10Timer(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        multiplayerGameRound10(roomID);/// create this method
                    }
                },
                countDown * 1000 // Delay in milliseconds (60 seconds)
        );
    }

    /**
     * Displays the summary of the multiplayer game.
     *
     * @param countDown The countdown duration in seconds.
     * @param roomID    The ID of the room for the multiplayer game.
     */

    public void showSummary(int countDown, int roomID) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        int score = getTotalRoundWinners();
                        clientApp.setTotalScore(score, roomID);
                        setRoundScore(roomID);
                        CalculatingScore calculatingScore = new CalculatingScore();
                        calculatingScore.setLocationRelativeTo(null);
                        calculatingScore.setResizable(false);
                        calculatingScore.setVisible(true);

                        javax.swing.Timer stopper = new javax.swing.Timer(3000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                calculatingScore.dispose();
                                System.out.println("testerssssssss: " + score + roomID);
                                Object gameLeads = clientApp.getCurrentGameLeaderboards(roomID);

                                if (gameLeads instanceof String[]) {
                                    System.out.println("game leads: " + Arrays.toString((String[]) gameLeads));
                                } else if (gameLeads instanceof Collection) {
                                    System.out.println("game leads: " + gameLeads);
                                } else {
                                    System.out.println("game leads: " + gameLeads.toString());
                                }

                                saveGameLeads(gameLeads);

                                if (gameLeads instanceof String[]) {
                                    String[] leadsArray = (String[]) gameLeads; // Cast to String[]
                                    String gameLeadsString = String.join(", ", leadsArray); // Convert array to string
                                    if (areScoresUnique(gameLeadsString)) {
                                        showGameSummaryMultiPlayer();
                                    } else {
                                        result.setVisible(false);
//                                        startTieBreakerTimer(0, roomID);
                                    }
                                } else if (gameLeads instanceof Collection) {
                                    Collection<?> leadsCollection = (Collection<?>) gameLeads;
                                    String gameLeadsString = String.join(", ", leadsCollection.toArray(new String[0])); // Convert collection to string
                                    if (areScoresUnique(gameLeadsString)) {
                                        showGameSummaryMultiPlayer();
                                    } else {
                                        result.setVisible(false);
//                                        startTieBreakerTimer(0, roomID);
                                    }
                                } else {
                                    System.out.println("Unexpected game leads format: " + gameLeads);
                                }

                                ((javax.swing.Timer) e.getSource()).stop(); // Stop the timer when done
                            }
                        });
                        stopper.start();
                    }
                },
                countDown * 1000
        );
    }

    /**
     * Gets the high score of the user.
     *
     * @param username The username of the user.
     * @return The high score of the user.
     */

    public static int getHighScore(String username) {
        int highScore = clientApp.getHighScore(username);
        return highScore;
    }

    /**
     * Sets the high score of the user.
     */

    public static void setHighScore() {
        String username = clientApp.getUserDetails();
        int words = countValidWords();
        int highScore = clientApp.getHighScore(username);
        if (words > highScore) {
            clientApp.setHighScore(words);
        }
    }

    /**
     * Sets the round score for the multiplayer game.
     *
     * @param roomID The ID of the room for the multiplayer game.
     */

    public static void setRoundScore(int roomID) {
        int words = countValidWordsPerRound();
        clientApp.setRoundScore(words, roomID);
        System.out.println("round score: " + words);
    }

    /**
     * Sets the time for the game.
     */

    public void setTime(){
        int time = clientApp.setTime();
        this.time = time;
        game.setRemainingSeconds(time);
    }

    /**
     * Displays the leaderboards.
     */

    public void displayLeaderBoards(){
        String[] menuLeaderboard = clientApp.menuLeaderboard();
        leaderboards.updateLeaderboardLabels(menuLeaderboard);
    }

    /**
     * Saves the game leads retrieved from the server.
     *
     * @param gameLeads The game leads to be saved.
     */

    private void saveGameLeads(Object gameLeads) {
        if (gameLeads instanceof Object[]) {
            Object[] leaderboardEntries = (Object[]) gameLeads;
            StringBuilder stringBuilder = new StringBuilder();
            for (Object entry : leaderboardEntries) {
                if (entry instanceof String) {
                    stringBuilder.append(entry).append(", ");
                }
            }
            String allEntries = stringBuilder.toString().trim();
            // Remove the trailing comma and space if there are entries
            if (!allEntries.isEmpty()) {
                allEntries = allEntries.substring(0, allEntries.length() - 2);
            }
            // Store all leaderboard entries as a single string
            this.gameLeads = allEntries;
        } else {
            System.out.println("Unexpected game leads format: " + gameLeads);
        }
    }

    /**
     * Retrieves the game leads.
     *
     * @return The game leads.
     */

    public static Object getGameLeads() {
//        System.out.println("gameleadsglobal:"+gameLeads);
        return gameLeads;
    }

    /**
     * Checks if the scores in the leaderboard are unique.
     *
     * @param gameLeadsString The string representation of the game leads.
     * @return True if all scores are unique, false otherwise.
     */

    public static boolean areScoresUnique(String gameLeadsString) {
        // Debug: Print the initial gameLeadsString
        System.out.println("Initial gameLeadsString: " + gameLeadsString);

        // Remove the brackets at the start and end if present
        gameLeadsString = gameLeadsString.trim();
        if (gameLeadsString.startsWith("[") && gameLeadsString.endsWith("]")) {
            gameLeadsString = gameLeadsString.substring(1, gameLeadsString.length() - 1);
        }

        // Split the string by the comma to separate each lead
        String[] leads = gameLeadsString.split(",");

        Set<Integer> uniqueScores = new HashSet<>();

        for (String lead : leads) {
            // Debug: Print the current lead being processed
            System.out.println("Processing lead: " + lead);

            // Split by pipe character to get each part
            String[] leadData = lead.trim().split("\\|");

            for (String part : leadData) {
                // Trim and check if the part contains the score
                part = part.trim();
                if (part.startsWith("TotalScore:")) {
                    // Extract the score value
                    int score = Integer.parseInt(part.substring("TotalScore:".length()).trim());

                    // Debug: Print the extracted score
                    System.out.println("Extracted score: " + score);

                    // Check for duplicates in the set
                    if (!uniqueScores.add(score)) {
                        // Debug: Print the status when a duplicate score is found
                        System.out.println("Duplicate score found: " + score);
                        return false; // Duplicate score found
                    }
                }
            }

            // Debug: Print the uniqueScores set after processing each lead
            System.out.println("Current uniqueScores set: " + uniqueScores);
        }

        // Debug: Print the final uniqueScores set
        System.out.println("Final uniqueScores set: " + uniqueScores);
        return true; // All scores are unique
    }

    /**
     * Compares scores of players for a specific round in the multiplayer game.
     *
     * @param roomID      The ID of the room for the multiplayer game.
     * @param roundNumber The round number for which scores are compared.
     */

    public void compareScores(int roomID, int roundNumber) {
        String[] scores;
        switch (roundNumber) {
            case 1:
                scores = clientApp.getRound1Score(roomID);
                break;
            case 2:
                scores = clientApp.getRound2Score(roomID);
                break;
            case 3:
                scores = clientApp.getRound3Score(roomID);
                break;
            case 4:
                scores = clientApp.getRound4Score(roomID);
                break;
            case 5:
                scores = clientApp.getRound5Score(roomID);
                break;
            case 6:
                scores = clientApp.getRound6Score(roomID);
                break;
            case 7:
                scores = clientApp.getRound7Score(roomID);
                break;
            case 8:
                scores = clientApp.getRound8Score(roomID);
                break;
            case 9:
                scores = clientApp.getRound9Score(roomID);
                break;
            case 10:
                scores = clientApp.getRound10Score(roomID);
                break;
            default:
                System.out.println("Invalid round number: " + roundNumber);
                return;
        }

        Map<String, Integer> scoreMap = new HashMap<>();
        Set<Integer> uniqueScores = new HashSet<>();
        boolean isDraw = false;

        for (String scoreEntry : scores) {
            String[] parts = scoreEntry.split("\\|"); // Split using pipe character
            String username = null;
            int score = 0;
            for (String part : parts) {
                String[] keyValue = part.split(":"); // Split key-value pairs
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    if ("Username".equalsIgnoreCase(key)) {
                        username = value;
                    } else if (("Round" + roundNumber + "Score").equalsIgnoreCase(key)) {
                        try {
                            score = Integer.parseInt(value);
                        } catch (NumberFormatException e) {
                            System.err.println("Invalid score format for entry: " + value);
                            score = 0; // Handle invalid score format
                        }
                    }
                }
            }
            if (username != null) {
                scoreMap.put(username, score);
                if (!uniqueScores.add(score)) {
                    isDraw = true; // If score is not unique, it's a draw
                }
            }
        }

        String currentUser = clientApp.getUserDetails(); // Get the current username

        if (isDraw) {
            game.jLabel8.setVisible(true);
            // Set a timer to hide the draw indicator after 5 seconds (5000 milliseconds)
            Timer timer = new Timer(5000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    game.jLabel8.setVisible(false);
                }
            });
            timer.setRepeats(false); // Only execute once
            timer.start();
        } else {
            // No draw, mark the current user if they are the top player
            String topPlayer = null;
            for (Map.Entry<String, Integer> entry : scoreMap.entrySet()) {
                if (topPlayer == null || entry.getValue() > scoreMap.get(topPlayer)) {
                    topPlayer = entry.getKey();
                    topPlayerUsername.add(topPlayer);
                }
            }

            if (currentUser.equals(topPlayer)) {
                if (!game.jLabel5.isVisible()) {
                    game.jLabel5.setVisible(true);
                } else if (!game.jLabel6.isVisible()) {
                    game.jLabel6.setVisible(true);
                } else {
                    game.jLabel7.setVisible(true);
                }
                roundWinners.add(currentUser);
            }
        }
    }

    /**
     * Gets the total number of round winners.
     *
     * @return The total number of round winners.
     */

    public int getTotalRoundWinners() {
        System.out.println("Round winner size: " + roundWinners.size() + ", Round winner list: " + roundWinners);
        return roundWinners.size();
    }
}