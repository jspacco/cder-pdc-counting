# Ideas

## Distributed counting
* POST /count with a unique ID and a number
* GET /count/{id} to retrieve the count for that ID

## Distributed Voting Game
* NVKC
    * 1 to N
    * first V votes wins
    * at least K votes required to win
    * after C votes we shut down voting
### Students
* POST /login with ?username=username&password=password
    * redirect to /current/{id}
* GET /current/{id} to go to the current vote
    * contains a form with what to vote on
* GET /vote/{id}/{vote_num} to get the form for the given vote_num
* POST /vote/{id}/{vote_num}/{num} to cast a vote


### faculty
* POST /config/{min}/{max}/{min_votes}/{max_votes} to set the min/max numbers to vote, and the min/max votes required
* GET /results/{vote_num} retrieves vote map for that vote
    * each number and the number of votes
    * was there a winner?
* GET /vote/{id}/{number} to retrieve the votes for that ID

# Distributed Fishing
* 2D grid with squares to click
* each square has points in it
    * if more than one person clicks the 