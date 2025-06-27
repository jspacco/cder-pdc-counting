package cder.pdc.counting;

import java.util.UUID;

public class UserSession {
    private UUID id;
    private String username;
    private int currentVoteNum;

    public UserSession(String username) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.currentVoteNum = 1;
    }

    public UserSession(String username, UUID id) {
        this.id = id;
        this.username = username;
        this.currentVoteNum = 1;
    }


    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public int getCurrentVoteNum() {
        return currentVoteNum;
    }

    public void setCurrentVoteNum(int currentVoteNum) {
        this.currentVoteNum = currentVoteNum;
    }

    public void incrementVoteNum() {
        this.currentVoteNum++;
    }

}
