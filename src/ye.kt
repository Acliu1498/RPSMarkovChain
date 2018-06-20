
import jdk.nashorn.api.tree.Tree
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.collections.HashMap

class MarkovChain(){
    //initalizes past choices randomly
    private var pastChoices: Array<Choice> = Array(5,{ _ -> Choice.values()[Random().nextInt(3)]})
    private var chain: HashMap<Choice, HashMap<Choice,Double>>

    init {

        this.chain = hashMapOf(Choice.ROCK to HashMap(), Choice.PAPER to HashMap(), Choice.SCISSORS to HashMap())
        updateChain()
    }

    /**
     * This is a function to update the markov chain
     */
    fun updateChain(){
        // sets each number in the chain equal to the number of times it appears in past guess
        for(i in 0.until(pastChoices.size-1)){
            // selects the index of the enum of the choice in the chain and adds one
            chain[pastChoices[i]]!![pastChoices[i+1]] = if(chain[pastChoices[i]]!![pastChoices[i+1]] != null) {
                chain[pastChoices[i]]!![pastChoices[i + 1]]!!.toInt() + 1.0
            } else {
                1.0
            }

        }

        for(choice in chain.keys){
            // find total num of choices made after this choice
            var tot = 0
            for (next in chain[choice]!!.keys) {
                tot += chain[choice]!![next]!!.toInt()
            }

            // updates the probability of each
            for(next in chain[choice]!!.keys){
                //sets the probablility
                chain[choice]!![next] = chain[choice]!![next]!!.toDouble() / tot.toDouble()
            }

        }
    }

    /**
     * This function updates the past choices given a new choice
     */
    fun updateChoices(choice: Choice){
        // removes furthest choices
        for(i in 0.until(pastChoices.size - 1)){
            pastChoices[i] = pastChoices[i + 1]
        }
        // updates the last choice
        pastChoices[pastChoices.size - 1] = choice
    }

    /**
     * makes a new guess based on chain
     */
    fun makeGuess(): Choice?{
        val pastChoice = pastChoices[pastChoices.size - 1]
        val tmp = chain[pastChoice]!!.toList().sortedBy({(_, value) -> value}).toMap()
        var guess = Random().nextInt(100).toDouble() / 100.0
        var sum = 0.0
        for(choice in tmp.keys){
            sum += tmp[choice]!!
            if(guess <= sum){
                return Choice.values()[if(choice.ordinal + 1 > 2)
                { 0
                } else {
                    choice.ordinal + 1
                }]
            }
        }
        return Choice.values()[0]
    }

}

fun main(args: Array<String>){
    var cont = true // bool to check if player wants to play more
    var mChain = MarkovChain()

    while (cont){
        // queries user
        println("Rock, Paper or Scissors?")
        // reads user response
        try {
            val userResponse: Choice = Choice.valueOf(readLine()!!.toUpperCase())

            // generates computer guess
            val com = mChain.makeGuess()
            println("Comp chose ${com}")
            // evaluates
            val res = userResponse.ordinal - com!!.ordinal
            // checks result
            if (res == 1 || res == -2) {
                println("You Win!!")
            } else if (res == -1 || res == 2) {
                println("You Lose!!")
            } else {
                println("Its a Tie!!")
            }
            mChain.updateChoices(userResponse)
            mChain.updateChain()
            // checks if user wants to play again
            println("Play Again?(y/n)")
            cont = readLine() == "y"

        } catch (e: IllegalArgumentException) {
            // if user does not pick viable answer continues
            println("You cant choose that.")
        }
    }
    println("Goodbye")
}


enum class Choice {ROCK, PAPER, SCISSORS}
