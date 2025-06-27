package cder.pdc.counting;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Controller
public class VoteController 
{

    private final VoteManager manager;
    private final UserRegistry userRegistry;
    private final VoteState voteState;

    public VoteController(VoteManager manager, UserRegistry userRegistry, VoteState voteState) {
        this.manager = manager;
        this.userRegistry = userRegistry;
        this.voteState = voteState;
    }

    @GetMapping("/")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String username, Model model) {
        if (!userRegistry.isValid(username)) {
            model.addAttribute("error", "Invalid username.");
            return "login";
        }

        UUID fixedId = userRegistry.getId(username);
        UUID id = manager.createSession(username, fixedId);
        return "redirect:/current/" + id;
    }

    @GetMapping("/current/{id}")
    public ResponseEntity<Void> current(@PathVariable UUID id) {
        var session = manager.getSession(id).orElseThrow();
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create("/vote/" + id + "/" + session.getCurrentVoteNum()))
                .build();
    }

    @GetMapping("/vote/{id}/{vote_num}")
    public String voteForm(@PathVariable UUID id,
                           @PathVariable int vote_num,
                           Model model)
    {
        var session = manager.getSession(id).orElseThrow();
        VoteRound round = manager.getOrCreateRound(vote_num);

        model.addAttribute("id", id);
        model.addAttribute("voteNum", vote_num);
        model.addAttribute("max", round.getN());

        return "vote";
    }

    @PostMapping("/vote/{id}/{voteNum}/{num}")
    public String submitVote(@PathVariable UUID id,
                             @PathVariable int voteNum,
                             @PathVariable int num,
                             RedirectAttributes redirectAttributes)
    {
        var session = manager.getSession(id).orElseThrow();
        // possible outcomes after a vote:
        // 1. User has already voted in this round
        // 2. Vote is recorded successfully, more votes needed
        // 3. Round is over, vote was too late (more than C votes)
        // 4. illegal vote but you can try to vote again
        VoteResult outcome = manager.vote(id, session.getUsername(), voteNum, num);

        if (outcome == VoteResult.ALREADY_VOTED) {
            redirectAttributes.addAttribute("message", "You already voted in this round.");
            // go to the results page
            return "redirect:/results/" + voteNum + "?id=" + id;
        } else if (outcome == VoteResult.ROUND_OVER) {
            redirectAttributes.addAttribute("message", "This round is over, you cannot vote anymore.");
            // go to the results page
            return "redirect:/results/" + voteNum + "?id=" + id;
        } else if (outcome == VoteResult.ILLEGAL_VOTE) {
            redirectAttributes.addAttribute("message", "Illegal vote, please try again.");
            redirectAttributes.addAttribute("max", manager.getOrCreateRound(voteNum).getN());
            // re-render vote form with error message
            return "redirect:/vote/" + id + "/" + voteNum;
        }

        // otherwise, the vote was successful
        redirectAttributes.addFlashAttribute("message", "Vote recorded successfully!");
        // go to results page
        return "redirect:/results/" + voteNum + "?id=" + id;
    }


    @GetMapping("/results/{voteNum}")
    public String getResults(@PathVariable int voteNum,
                             @RequestParam UUID id,
                             @ModelAttribute(name = "message", binding = false) String message,
                             Model model) 
    {
        if (message != null && !message.isEmpty()) {
            model.addAttribute("message", message);
        }
        
        List<VoteRecord> votes = manager.getResults(voteNum);
        List<VoteRecord> winners = manager.getWinners(voteNum);
        boolean noWinner = manager.getRound(voteNum).isMaxVotesReached() && winners.isEmpty();

        model.addAttribute("voteNum", voteNum);
        model.addAttribute("votes", votes);
        model.addAttribute("winners", winners);
        model.addAttribute("noWinner", noWinner);
        model.addAttribute("id", id);

        return "results";
    }


    @PostMapping("/next/{id}/{vote_num}")
    public String nextRound(@PathVariable UUID id,
                            @PathVariable int vote_num) {
        var session = manager.getSession(id).orElseThrow();

        // Only allow advancing from current round
        if (session.getCurrentVoteNum() == vote_num) {
            session.incrementVoteNum();
        }

        return "redirect:/vote/" + id + "/" + session.getCurrentVoteNum();
    }

}
