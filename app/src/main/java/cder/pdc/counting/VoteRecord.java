package cder.pdc.counting;

import java.time.Instant;
import java.util.UUID;

public class VoteRecord 
{
    private final UUID userId;
    private final String username;
    private final int voteNum;
    private final int numberVotedFor;
    private final Instant timestamp;

    public VoteRecord(UUID userId, String username, int voteNum, int numberVotedFor) {
        this.userId = userId;
        this.username = username;
        this.voteNum = voteNum;
        this.numberVotedFor = numberVotedFor;
        this.timestamp = Instant.now();
    }

    public UUID getUserId() {
        return userId;
    }

    public int getVoteNum() {
        return voteNum;
    }

    public int getNumberVotedFor() {
        return numberVotedFor;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getUsername() {
        return username;
    }
}
