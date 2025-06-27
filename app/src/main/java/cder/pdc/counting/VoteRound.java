package cder.pdc.counting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class VoteRound 
{
    // pick a number from 1 to N
    private final int N;
    // first V votes for the same number win
    private final int V;
    // at least K votes required to win
    private final int K;
    // after C votes we shut down voting
    private final int C;

    private final Map<Integer, List<VoteRecord>> voteBuckets = new HashMap<>();
    private final List<VoteRecord> allVotes = new ArrayList<>();
    private final Set<UUID> alreadyVoted = new HashSet<>();


    public VoteRound(int N, int V, int K, int C) {
        this.N = N;
        this.V = V;
        this.K = K;
        this.C = C;
    }

    public synchronized boolean hasVoted(UUID userId) {
        // Check if the user has already voted
        return alreadyVoted.contains(userId);
    }

    public synchronized boolean isWinner() {
        // Check if any bucket has enough votes to be a winner
        for (var entry : voteBuckets.entrySet()) {
            if (entry.getValue().size() >= K) {
                // We have a winner
                return true; 
            }
        }
        // No winner yet
        return false;
    }

    public synchronized boolean isMaxVotesReached() {
        // Check if we have reached the max number of votes
        return allVotes.size() >= C; 
    }

    public synchronized boolean isGameOver() {
        // game is over if we have a winner 
        // or if we reached the max votes
        return isWinner() || isMaxVotesReached();
    }

    public synchronized boolean recordVote(VoteRecord record) {
        if (alreadyVoted.contains(record.getUserId())) {
            // already voted
            return false; 
        }
        //TODO: don't allow votes if the game is over
        allVotes.add(record);
        alreadyVoted.add(record.getUserId());
        voteBuckets.computeIfAbsent(record.getNumberVotedFor(), k -> new ArrayList<>()).add(record);
        
        return true;
    }

    public List<VoteRecord> getAllVotes() {
        return allVotes;
    }

    public List<VoteRecord> getWinners() {
        for (var entry : voteBuckets.entrySet()) {
            List<VoteRecord> votes = entry.getValue();
            if (votes.size() >= K) {
                return votes.subList(0, Math.min(K, votes.size()));
            }
        }
        return List.of();
    }

    public int getN() {
        return N;
    }
}
