package cder.pdc.counting;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VoteManager 
{
    private final Map<UUID, UserSession> sessions = new ConcurrentHashMap<>();
    private final Map<Integer, VoteRound> rounds = new ConcurrentHashMap<>();

    private final int N;
    private final int K;
    private final int C;
    private final int V;

    public VoteManager(VoteState state) {
        // Number of choices
        this.N = state.getN(); 
        // Votes needed to win
        this.V = state.getV(); 
        // Minimum votes to be a winner
        this.K = state.getK(); 
        // Max votes before round ends
        this.C = state.getC(); 
    }

    public UUID createSession(String username, UUID fixedId) {
        UserSession session = new UserSession(username, fixedId);
        sessions.put(fixedId, session);
        return fixedId;
    }


    public Optional<UserSession> getSession(UUID id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public VoteRound getOrCreateRound(int voteNum) {
        return rounds.computeIfAbsent(voteNum, n -> new VoteRound(N, V, K, C));
    }

    public VoteResult vote(UUID userId, String username, int voteNum, int choice) {
        VoteRound round = getOrCreateRound(voteNum);

        // already voted
        if (round.hasVoted(userId)) return VoteResult.ALREADY_VOTED;

        // round is over
        if (round.isMaxVotesReached()) return VoteResult.ROUND_OVER;

        // illegal vote but can try again
        if (choice < 1 || choice > round.getN()) return VoteResult.ILLEGAL_VOTE;

        round.recordVote(new VoteRecord(userId, username, voteNum, choice));

        return VoteResult.VOTE_SUCCESS;
    }

    public VoteRound getRound(int voteNum) {
        return rounds.get(voteNum);
    }


    public List<VoteRecord> getResults(int voteNum) {
        return getOrCreateRound(voteNum).getAllVotes();
    }

    public List<VoteRecord> getWinners(int voteNum) {
        return getOrCreateRound(voteNum).getWinners();
    }
}
