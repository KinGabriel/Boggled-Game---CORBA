/**
 * The clientApp class represents the client application for the Boggled game.
 * It connects to the Boggled server using CORBA, provides functionalities like
 * login, registration,updating profiles and game mechanics.
 */

package Client;
import boggledApp.*;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CORBA.Object;
import org.omg.CosNaming.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

import javax.swing.*;
import java.io.IOException;


public class clientApp {
    private static Boggled servant;
    private static clientApp instance;
    private static String currentUser;

     /**
     * Main method of the client application. Initializes the GUI and shows the
     * IP entry dialog.
     * @param args Command line arguments
     */

    public static void main(String args[]) {
        setNimbusLookAndFeel();
        SwingUtilities.invokeLater(() -> {
            Controller.getInstance().showIP();
        });
    }

     /**
     * Runs the client application by connecting to the server with the provided IP.
     * @param ip IP address of the server
     * @return The IP address used to connect to the server
     */

    public static String run(String ip) {
        try {
            String[] args = new String[]{"orbd", "-ORBInitialPort", "1050", "-ORBInitialHost", ip};
            ORB orb = ORB.init(args, null);
            Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            String name = "Boggled";
            servant = BoggledHelper.narrow(ncRef.resolve_str(name));
            JOptionPane.showMessageDialog(null, "Successfully entered the server!", "Success", JOptionPane.INFORMATION_MESSAGE);
            SwingUtilities.invokeLater(() -> {
                Controller.getInstance().showLogin();
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Invalid IP! Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return ip;
    }

     /**
     * Retrieves the singleton instance of the clientApp class.
     * @return The singleton instance of clientApp
     */

    public static clientApp getInstance() {
        if (instance == null) {
            instance = new clientApp();
        }
        return instance;
    }

     /**
     * Retrieves the singleton instance of the clientApp class.
     * @return The singleton instance of clientApp
     */

    private static void setNimbusLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace(); // Log any errors encountered while setting the Look and Feel
        }
    }
     /**
     * Logs in a user with the provided credentials.
     * @param username Username of the user
     * @param password Password of the user
     * @return True if login is successful, otherwise false
     * @throws userAlreadyLogInException If the user is already logged in
     * @throws invalidCredentialsException If the provided credentials are invalid
     * @throws bannedAccountException If the user's account is banned
     * @throws InvalidName If the name is invalid
     * @throws AdapterInactive If the adapter is inactive
     * @throws WrongPolicy If the policy is wrong
     * @throws ServantNotActive If the servant is not active
     */

    public boolean login(String username, String password) throws userAlreadyLogInException, invalidCredentialsException, bannedAccountException, InvalidName, AdapterInactive, WrongPolicy, ServantNotActive {

        boolean validateLogIn = servant.login(username, password);
        this.currentUser = username;
        return validateLogIn;

    }

     /**
     * Registers a new user with the provided credentials.
     * @param username Username of the new user
     * @param password Password of the new user
     * @return True if registration is successful, otherwise false
     * @throws usernameAlreadyExistsException If the provided username already exists
     */

    public static boolean register(String username,String password) throws usernameAlreadyExistsException {
           return servant.register(username,password);

    }

     /**
     * Updates the profile of the current user with new credentials.
     * @param username Username of the current user
     * @param password New password for the current user
     * @return True if profile update is successful, otherwise false
     */

    public static boolean updateProfile(String username,String password) {
        return servant.updateCredential(username,password);
    }

     /**
     * Creates a single-player game.
     * @return An array containing game information for single-player mode
     */

    public String[] createGameSinglePlayer(){
        return servant.createGameSinglePlayer();
    }

     /**
     * Retrieves the avatar of the current player.
     * @return The avatar ID of the current player
     */

    public  int getPlayerAvatar(){
        return servant.getPlayerAvatar(currentUser);
    }

     /**
     * Updates the avatar of the current player.
     * @param avatar The new avatar ID for the player
     * @return True if the avatar update is successful, otherwise false
     */

    public boolean updatePlayerAvatar(int avatar){
        return servant.updatePlayerAvatar(currentUser,avatar);
    }

     /**
     * Retrieves details of the current user.
     * @return The username of the current user
     */

    public static String getUserDetails(){
        return currentUser;
    }

     /**
     * Checks if a word is valid in the game.
     * @param word The word to be checked
     * @param roomID The ID of the game room
     * @return True if the word is valid, otherwise false
     * @throws invalidWordException If the word is invalid
     */

    public boolean checkWord(String word,int roomID) throws invalidWordException {
        return servant.verifyWord(roomID, word,currentUser);
    }

     /**
     * Calculates the points for a given word.
     * @param words The word for which points are to be calculated
     * @return The points earned for the word
     */

    public static int wordPoints(String words) {
        return servant.pointsPerWord(words);
    }

     /**
     * Logs out the current user.
     * @return True if logout is successful, otherwise false
     * @throws IOException If an I/O error occurs
     * @throws failedLogOutException If logout fails
     */

    public Boolean logout() throws IOException, failedLogOutException {
        Boolean response = servant.logout(currentUser);
        this.currentUser = null;
        return response;
    }

     /**
     * Retrieves a list of available game rooms.
     * @return An array containing information about available game rooms
     */

    public String[] listOfRooms(){
            return servant.showListOfRooms();
    }

     /**
     * Sends a request to join a game room.
     * @param roomID The ID of the game room
     * @return The remaining time in seconds until the game starts
     * @throws roomDoesntExistException If the specified room does not exist
     */

    public int sendRoom(int roomID)throws roomDoesntExistException{
        int secs = servant.inputRoom(roomID,currentUser);
         return  secs;
    }

     /**
     * Creates a new game room.
     * @return An array containing information about the newly created game room
     */

    public int[] createRoom(){
        return servant.createGameMultiPlayer(currentUser);
    }

     /**
     * Leaves the specified game room.
     * @param roomID The ID of the game room to leave
     * @return True if leaving the room is successful, otherwise false
     */

    public boolean leaveRoom(int roomID){
        boolean leave = servant.leaveRoom(roomID,currentUser);
        return leave;
    }

     /**
     * Retrieves the characters of the players in a multiplayer game.
     * @param roomID The ID of the game room
     * @return An array containing characters of the players in the specified game room
     */

    public String[] multiplayerChars(int roomID){
        return servant.getCharacters(roomID);
    }

     /**
     * Retrieves the high score of the specified user.
     * @param currentUser The username of the user
     * @return The high score of the user
     */

    public int getHighScore(String currentUser) {
        int highScore = servant.getHighScore(currentUser);
        return highScore;
    }

     /**
     * Sets the high score of the current user.
     * @param score The new high score to be set
     */

    public void setHighScore(int score) {
        servant.setHighScore(currentUser, score);
    }

     /**
     * Sets the round score for the current user in a specific game room.
     * @param score The round score to set
     * @param roomID The ID of the game room
     */

    public void setRoundScore(int score, int roomID) {
        servant.setRoundScore(currentUser, score, roomID);
    }

     /**
     * Retrieves the time for the game.
     * @return The time for the game
     */

    public int setTime(){
        return servant.setTime();
    }

     /**
     * Retrieves the time for the game.
     * @return The time for the game
     */

    public void closeRoom(int roomID) throws roomDoesntExistException {
        servant.closeRoom(roomID);
    }

     /**
     * Sets the total score for the current user in a specific game room.
     * @param score The total score to set
     * @param roomID The ID of the game room
     */

    public void setTotalScore(int score, int roomID) {
        servant.setTotalScore(currentUser, score, roomID);
    }

     /**
     * Retrieves the current game leaderboards for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the current game leaderboards
     */

    public String[] getCurrentGameLeaderboards(int roomID) {
        return  servant.getCurrentGameLeaderboards(roomID);
    }

     /**
     * Retrieves the scores of round 1 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 1
     */

    public String[] getRound1Score(int roomID) {
        return servant.getRound1Score(roomID);
    }

     /**
     * Retrieves the scores of round 2 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 2
     */

    public String[] getRound2Score(int roomID) {
        return servant.getRound2Score(roomID);
    }

     /**
     * Retrieves the scores of round 3 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 3
     */

    public String[] getRound3Score(int roomID) {
        return servant.getRound3Score(roomID);
    }

     /**
     * Retrieves the scores of round 4 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 4
     */

    public String[] getRound4Score(int roomID) {
        return servant.getRound4Score(roomID);
    }

     /**
     * Retrieves the scores of round 5 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 5
     */

    public String[] getRound5Score(int roomID) {
        return servant.getRound5Score(roomID);
    }

     /**
     * Retrieves the scores of round 6 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 6
     */

    public String[] getRound6Score(int roomID) {
        return servant.getRound6Score(roomID);
    }

     /**
     * Retrieves the scores of round 7 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 7
     */

    public String[] getRound7Score(int roomID) {
        return servant.getRound7Score(roomID);
    }

     /**
     * Retrieves the scores of round 8 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 8
     */

    public String[] getRound8Score(int roomID) {
        return servant.getRound8Score(roomID);
    }

     /**
     * Retrieves the scores of round 9 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 9
     */

    public String[] getRound9Score(int roomID) {
        return servant.getRound9Score(roomID);
    }

     /**
     * Retrieves the scores of round 10 for a specific game room.
     * @param roomID The ID of the game room
     * @return An array containing the scores of round 10
     */

    public String[] getRound10Score(int roomID) {
        return servant.getRound10Score(roomID);
    }

     /**
     * Retrieves the leaderboard data for the main menu.
     * @return An array containing leaderboard data for the main menu
     */

    public String[] menuLeaderboard(){
        return servant.getLeaderboard();
    }

     /**
     * Checks if there is another player in the room.
     * @param roomID The ID of the game room
     * @return True if all players have joined, otherwise false
     */

    public boolean checkPlayers(int roomID){
        return servant.checkPlayerJoined(roomID);
    }

     /**
     * Notify other user if the game is done.
     * @param roomID The ID of the game room
     * @return True if all players have joined, otherwise false
     */

    public void sendDone(int roomID) {
        servant.sendDone(roomID);
    }

     /**
     * Checks if the game is done.
     * @param roomID The ID of the game room
     * @return True if all players have joined, otherwise false
     */

    public boolean checkGameDone(int roomID) {
        return servant.checkGameDone(roomID);
    }
}