package Hangman_Multiplatform_Group.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import java.io.File

//@Composable
//fun App() {
//    var text by remember { mutableStateOf("Hello, World!") }
//    val platformName = getPlatformName()
//
//    Button(onClick = {
//        text = "Hello, ${platformName}"
//    }) {
//        Text(text)
//    }
//}
@Composable
fun App() {
    //playHangman("App")
    //playHangmanHighScore()
    val hm = HangmanGame()
    hm.playHangmanSentScoreToServer()
}

@Composable
fun askNameSentToServer(score: Int) {
    var playerName by remember { mutableStateOf("") }
    var sentToServer by remember { mutableStateOf(false) }
    Column {
        TextField(
            value = playerName,
            onValueChange = {
                if (it.isNotEmpty()) {
                    playerName = it
                }

            },
            label = { Text(text = "Type your name") },
            placeholder = { Text(text = "Your name") },
        )
        Button(onClick = { if (!sentToServer)
                {sentToServer = true }})
        { Text("Send name = " + playerName + " score =" + score + " to server") }

    }

}

@Composable
fun playHangmanHighScore() {
    var score by remember { mutableStateOf(0) }
    //Text(score.toString())
    var noGameLost by remember { mutableStateOf(true) }
    Row {
        if (noGameLost) {
            Column {
                playHangman("App", { score += 1 }, { noGameLost = false })
            }
            Text("Score: $score")
        }
        //if (lastGameWon){score+=1;noGameLost = lastGameWon;lastGameWon = false}
        //else{return}
        else {
            //Text("sent score $score to server!")
            //callServer()
            askNameSentToServer(score)
        }
    }

}

@Composable
fun playHangman(wordToGuess: String, callGameWon: () -> Unit, callGameLost: () -> Unit) {
    var gameLogic by remember { mutableStateOf(GameLogic(wordToGuess, 7)) }
    var knowLettersInWord by remember { mutableStateOf(gameLogic.showOnlyGuessedLettersInWord()) }
    var newInput by remember { mutableStateOf("") }
    var messageToUser by remember { mutableStateOf("") }
    val wordHelpers = WordHelpers()
    var gameWon by remember { mutableStateOf(false) }
    var gameLost by remember { mutableStateOf(false) }

    fun reset() {
        gameLogic = GameLogic(wordToGuess, 7)
        knowLettersInWord = gameLogic.showOnlyGuessedLettersInWord()
        newInput = ""
        messageToUser = ""
        gameWon = false
        gameLost = false

    }

    Column {
        //Text("")
        //Text("")
        Text("Das Wort ist:")
        Text(
            knowLettersInWord,
            fontSize = 200.sp,
            fontFamily = FontFamily.SansSerif,
            style = TextStyle(letterSpacing = 25.sp)
        )
        Text(messageToUser)
        TextField(
            value = newInput,
            onValueChange = {
                if (it.length <= 1) {
                    newInput = it
                }

            },
            label = { Text(text = "Type a single letter") },
            placeholder = { Text(text = "Your Guess") },
        )
        Button(onClick = {
            if (wordHelpers.isLetter(newInput) && !gameLost && !gameWon) {
                val (newMessage, newGameWon, newGameLost) = gameLogic.playRound(newInput[0]);
                messageToUser = newMessage
                knowLettersInWord = gameLogic.showOnlyGuessedLettersInWord()
                gameWon = newGameWon
                gameLost = newGameLost

            }


        }) {
            Text("Send")
        }

    }


    if (gameWon) {
        reset();callGameWon()

    }
    if (gameLost) {
        callGameLost()
    }
}


//fun main(args: Array<String>) {
//    println("Hello World!")
//
//    // Try adding program arguments via Run/Debug configuration.
//    // Learn more about running applications: https://www.jetbrains.com/help/idea/running-applications.html.
//    println("Program arguments: ${args.joinToString()}")
//
//    val hm = HangmanGame()
//    hm.playHangmanHighScore()
//}

class GameLogic(wordToGuess: String, tries: Int) {

    private var guessedLetters: MutableList<Char> = mutableListOf()
    private var wordToGuess: String = wordToGuess
    private var triesLeft: Int = tries


    fun showOnlyGuessedLettersInWord(): String {
        return String((wordToGuess.map { c ->
            if (guessedLetters.contains(c)) {
                c
            } else {
                '_'
            }
        }).toCharArray())
    }

    private fun gameIsWon(): Boolean {
        return showOnlyGuessedLettersInWord() == wordToGuess
    }

    private fun gameIsLost(): Boolean {
        return triesLeft < 1
    }

    private fun tryChar(newInput: Char): Boolean {
        if (wordToGuess.contains(newInput)) {
            guessedLetters.add(newInput)
            return true
        } else {
            triesLeft -= 1
            return false
        }

    }

    private fun makeMessage(guessedRight: Boolean): Triple<String, Boolean, Boolean> {
        var message: String
        if (guessedRight) {
            message = "You guessed right!"
            if (gameIsWon()) {
                message = "$message\nYou win!"
                return Triple(message, true, false)
            }
        } else {
            message = "You guessed wrong!"
            if (gameIsLost()) {
                message = "$message\nYou loose!"
                return Triple(message, false, true)
            }

        }
        return Triple("$message\n${triesLeft} Versuche übrig", false, false)
    }

    fun playRound(newInput: Char): Triple<String, Boolean, Boolean> {
        return makeMessage(tryChar(newInput))

    }

    fun getWordToGuess(): String {
        return wordToGuess
    }

}


class WordHelpers {
    private val germanWords: List<String> =
        File("./wordlist-german.txt").bufferedReader().readLines().filter { word -> word.length < 5 }

    fun isLetter(s: String): Boolean {
        return (s.length == 1) && s[0].isLetter()
    }

    fun isGermanWord(word: String): Boolean {

        return germanWords.contains(word)

    }

    fun randomGermanWord(): String {
        return germanWords.random()
    }

}


class TUI() {


    private val wordHelpers = WordHelpers()

    fun randomGermanWord(): String {
        return wordHelpers.randomGermanWord()
    }

    fun askForLetter(): Char {
        while (true) {
            println("Gib einen Buchstaben ein!")
            val maybeLetter: String = readln()
            if (wordHelpers.isLetter(maybeLetter)) {
                return maybeLetter[0]
            }
            println(maybeLetter + "ist kein Buchstabe")
        }

    }


    fun askForWord(): String {
        while (true) {
            println("Welches Wort soll erraten werden!")
            val maybeWord: String = readln()
            /*            if (maybeWord.all { c -> c.isLetter() }  ) {
                            return maybeWord
                        }*/
            if (wordHelpers.isGermanWord(maybeWord)) {
                return maybeWord
            }
            //println("Das Wort darf nur Buchstaben enthalten!")
            println("Das ist kein deutsches Wort!")
        }


    }

}

class HangmanGame() {
    private val tui = TUI()
    //private val gameLogic = GameLogic("Hallo", 3)

    fun playHangmanTUI(wordToGuess: String): Boolean {
        val gameLogic = GameLogic(wordToGuess, 7)
        //var gameOnGoing = true
        var gameWon = false
        var gameLost = false
        while (!(gameWon || gameLost)) {
            println("Das Wort ist: " + gameLogic.showOnlyGuessedLettersInWord())
            val newInput = tui.askForLetter()
            val (message: String, newGameWon: Boolean, newGameLost: Boolean) = gameLogic.playRound(newInput)
            println(message)
            gameWon = newGameWon
            gameLost = newGameLost
            //gameOnGoing = !(gameWon || gameLost)

        }
        println("Das Wort war: " + gameLogic.getWordToGuess())
        return gameWon

    }

    fun playHangmanTUI2Players(): Boolean {
        val wordToGuess: String = tui.askForWord()
        println("\n".repeat(100))
        return playHangmanTUI(wordToGuess)
    }

    fun playHangmanTUIOnePlayer(): Boolean {
        val wordToGuess: String = tui.randomGermanWord()
        //println("\n".repeat(100))
        return playHangmanTUI(wordToGuess)
    }


    fun playHangmanHighScore(): Int {
        var noGameLost = true
        var score = 0
        while (noGameLost) {
            val wonLastGame = playHangmanTUIOnePlayer()
            if (wonLastGame) {
                score += 1
            }
            noGameLost = wonLastGame
            println("Next Round!")

        }
        return score
    }

    fun playHangmanSentScoreToServer(){
        val score = playHangmanHighScore()
        println("Gib deinen Namen ein")
        val playerName = readln()
        println("Send name = " + playerName + " score =" + score + " to server")
    }


}