package Utilities;

public class user {
    private String username;
    private String password;
    private int userID;
    private int userAvatar;
    private String isActive;
    private String highScore;
    public user(String username,String password) {
        this.username = username;
        this.password = password;
    }

    public user(String username,String password, int userAvatar){
        this.username = username;
        this.password = password;
        this.userAvatar = userAvatar;
    }

    public user(String username,String highScore,String isActive) {
        this.username = username;
        this.highScore = highScore;
        this.isActive = isActive;
    }
    public int getUserID() {
        return userID;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public void setHighScore(String highScore) {
        this.highScore = highScore;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserAvatar(int userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getHighScore() {
        return highScore;
    }

    public int getUserAvatar() {
        return userAvatar;
    }

    public String getIsActive() {
        return isActive;
    }
}
