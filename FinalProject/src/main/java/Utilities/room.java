package Utilities;

public class room {
    private int roomID;
    private String status;
    private int count;
    private String host;

    public room(int roomID, String status, String host) {
        this.roomID = roomID;
        this.status = status;
        this.host = host;
    }
    public room(int roomID,String host,int count ) {
        this.roomID = roomID;
        this.count = count;
        this.host = host;
    }
    public String getHost() {
        return host;
    }

    public int getCount() {
        return count;
    }

    public int getRoomID() {
        return roomID;
    }

    public String getStatus() {
        return status;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public void incrementCount() {
        count++;
    }
}
