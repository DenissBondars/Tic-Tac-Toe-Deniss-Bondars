// This game was created by Deniss Bondars, 221RDB122

// Most of the information about how to design the game using drawable resource files (.xml),
// switch different windows, as well as the logic of creating the Tic Tac Toe game for PVP mode
// was taken from this video in which a Tic Tac Toe game was created in Java:
// https://www.youtube.com/watch?v=9hOJH7IflFo&t=585s
// Also, to simplify game development and to understand the correct syntax and learn commands in
// the Kotlin language, artificial intelligence "ChatGPT" was used for some code fragments,
// mostly for PVC mode realization.

// Link to GitHub project: https://github.com/DenissBondars/Tic-Tac-Toe-Deniss-Bondars.git

package com.example.tictactoegame

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var playerNameX: TextView // TextViews for the player names
    private lateinit var playerNameO: TextView

    private lateinit var playerLayoutX: LinearLayout // LinearLayouts represent custom player layouts
    private lateinit var playerLayoutO: LinearLayout

    private lateinit var field1: ImageView // ImageViews for the game board fields
    private lateinit var field2: ImageView
    private lateinit var field3: ImageView
    private lateinit var field4: ImageView
    private lateinit var field5: ImageView
    private lateinit var field6: ImageView
    private lateinit var field7: ImageView
    private lateinit var field8: ImageView
    private lateinit var field9: ImageView

    private lateinit var winnerTextView: TextView // TextView to display the winner

    private lateinit var menuButton: Button // Buttons for menu and restarting the game
    private lateinit var restartButton: Button

    // Initialize variables to control game state and player moves
    private var playerTurn = 1 // Indicates the current player's turn: 1 is X turn, 2 is O turn
    private var boxPos = IntArray(9) // Represents the state of each game board box: 0 is empty, 1 is X, 2 is O
    private var winnerName = "" // Stores the name of the winner
    private var gameOver = false // Flag to indicate if the game is over
    private var isPVC = false // Indicates whether the game is Player vs Computer mode
    private var isPlayerSymbolX = false // In PVC mode indicates whether the player's symbol is X
    private var isComputerThinking = false // In PVC mode this flag indicates whether the computer is currently making its turn

    // List of winning combinations to check who is a winner in a game
    private val winningCombinationsList = listOf(
        listOf(0, 1, 2), // Winning combinations for horizontal lines
        listOf(3, 4, 5),
        listOf(6, 7, 8),

        listOf(0, 3, 6), // Winning combinations for vertical lines
        listOf(1, 4, 7),
        listOf(2, 5, 8),

        listOf(0, 4, 8), // Winning combinations for diagonal lines
        listOf(2, 4, 6)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the layout views, player names, buttons and game board fields by their ID
        playerNameX = findViewById(R.id.playerNameX)
        playerNameO = findViewById(R.id.playerNameO)
        playerLayoutX = findViewById(R.id.playerLayoutX)
        playerLayoutO = findViewById(R.id.playerLayoutO)
        winnerTextView = findViewById(R.id.winnerTextView)
        restartButton = findViewById(R.id.restartButton)
        menuButton = findViewById(R.id.menuButton)
        field1 = findViewById(R.id.field1)
        field2 = findViewById(R.id.field2)
        field3 = findViewById(R.id.field3)
        field4 = findViewById(R.id.field4)
        field5 = findViewById(R.id.field5)
        field6 = findViewById(R.id.field6)
        field7 = findViewById(R.id.field7)
        field8 = findViewById(R.id.field8)
        field9 = findViewById(R.id.field9)

        // Set edited player names, picked game mode settings and player's selected symbol
        playerNameX.text = intent.getStringExtra("playerXName")
        playerNameO.text = intent.getStringExtra("playerOName")
        isPVC = intent.getBooleanExtra("isPVC", false)
        isPlayerSymbolX = intent.getBooleanExtra("isPlayerSymbolX", false)

        // Set starting player turn based on game mode,
        // where playerTurn = 1 means the first turn of the crosses,
        // and playerTurn = 2 means the turn of the zeros
        if (isPVC) {
            playerTurn = if (isPlayerSymbolX) 1 else 2
            currentPlayerTurn(playerTurn)
        } else {
            currentPlayerTurn(1)
        }

        // Set click listeners for all 9 game board fields, where it is checked whether the
        // computer is currently making a move in the case of PVC mode and whether the field where
        // the player clicks is free
        field1.setOnClickListener {
            if (!isComputerThinking && isFieldFree(0)) {
                takeAction(field1, 0)
            }
        }

        field2.setOnClickListener {
            if (!isComputerThinking && isFieldFree(1)) {
                takeAction(field2, 1)
            }
        }

        field3.setOnClickListener {
            if (!isComputerThinking && isFieldFree(2)) {
                takeAction(field3, 2)
            }
        }

        field4.setOnClickListener {
            if (!isComputerThinking && isFieldFree(3)) {
                takeAction(field4, 3)
            }
        }

        field5.setOnClickListener {
            if (!isComputerThinking && isFieldFree(4)) {
                takeAction(field5, 4)
            }
        }

        field6.setOnClickListener {
            if (!isComputerThinking && isFieldFree(5)) {
                takeAction(field6, 5)
            }
        }

        field7.setOnClickListener {
            if (!isComputerThinking && isFieldFree(6)) {
                takeAction(field7, 6)
            }
        }

        field8.setOnClickListener {
            if (!isComputerThinking && isFieldFree(7)) {
                takeAction(field8, 7)
            }
        }

        field9.setOnClickListener {
            if (!isComputerThinking && isFieldFree(8)) {
                takeAction(field9, 8)
            }
        }

        // Set click listener for restart button
        restartButton.setOnClickListener {
            restartGame()
        }

        // Set click listener for menu button
        menuButton.setOnClickListener {
            val intent = Intent(this, AddModes::class.java)
            startActivity(intent)
        }

        // At the beginning of the game it is checked if it's the computer's turn in PVE mode,
        // then computer must make the first move
        if (isPVC && playerTurn == 2) {
            makeComputerMove()
        }

    }

    // Boolean function to check if a game board field is free.
    // Returns true if the clicked game board field is free
    private fun isFieldFree(pos: Int): Boolean {
        return boxPos[pos] == 0
    }

    // Function to take action after player click on the game board field
    private fun takeAction(imageView: ImageView, selectedBoxPosition: Int) {

        // Check if game is not over (gameOver = false) and the selected game board field is free
        if (!gameOver && isFieldFree(selectedBoxPosition)) {

            // Mark the selected field with the current player's symbol in PVP and PVC mode.
            boxPos[selectedBoxPosition] = playerTurn
            if ((playerTurn == 1 && !isPVC) || (isPVC && isPlayerSymbolX)) {
                imageView.setImageResource(R.drawable.pic_x)
           }
            else {
                imageView.setImageResource(R.drawable.pic_o)
            }

            // Check for a winner or draw after each move
            if (checkWinner()) {
                // Find the winner name based on the current player's turn
                winnerName = if ((playerTurn == 1 && !isPVC) || (isPVC && isPlayerSymbolX))
                    playerNameX.text.toString() else playerNameO.text.toString()
                // Function call resetBorderHighlight() to reset player layout border highlights,
                // function call showWinner(winnerName) to display the winner and
                // assigning game end status (gameOver = true)
                resetBorderHighlight()
                showWinner(winnerName)
                gameOver = true
            }

            else if (checkDraw()) {
                // Function call resetBorderHighlight() to reset player layout border highlights,
                // function call showDraw() to display draw message and
                // assigning game end status (gameOver = true)
                resetBorderHighlight()
                showDraw()
                gameOver = true
            }
            else {
                // If the game is not over, then switch to the next player's turn
                currentPlayerTurn(if (playerTurn == 1) 2 else 1)
                // If the PVE mode is selected and if it's the computer's turn,
                // the function makeComputerMove() is called
                if (playerTurn == 2 && isPVC) {
                    makeComputerMove()
                }
            }
        }
    }

    // Function to make a move for the computer player
    private fun makeComputerMove() {
        // Set flag to indicate that the computer is thinking, so that the player cannot make
        // a move while the computer is artificially delaying the move
        isComputerThinking = true

        // Delay computer move to create artificial thinking time
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({

            // Determine the symbol for the computer player based on the game settings
            val computerSymbol = if (isPlayerSymbolX) {
                R.drawable.pic_o
            } else {
                R.drawable.pic_x
            }

            // Find all empty fields on the game board
            val emptyFields = mutableListOf<Int>()
            for (i in boxPos.indices) {
                if (boxPos[i] == 0) {
                    emptyFields.add(i)
                }
            }

            // Randomly select an empty game board field for the computer's move
            val randomFieldIndex = emptyFields.random()

            // Update the selected field with the computer's symbol
            when (randomFieldIndex) {
                0 -> field1.setImageResource(computerSymbol)
                1 -> field2.setImageResource(computerSymbol)
                2 -> field3.setImageResource(computerSymbol)
                3 -> field4.setImageResource(computerSymbol)
                4 -> field5.setImageResource(computerSymbol)
                5 -> field6.setImageResource(computerSymbol)
                6 -> field7.setImageResource(computerSymbol)
                7 -> field8.setImageResource(computerSymbol)
                8 -> field9.setImageResource(computerSymbol)
            }

            // Update the game state with the computer's move
            boxPos[randomFieldIndex] = playerTurn

            // Similar checks as for players to check for a winner or draw after computer's move
            if (checkWinner()) {
                // Function call resetBorderHighlight() to reset player's and computer's layout
                // border highlights, set the winner name to "Computer", function call
                // showWinner(winnerName) to display the winner and assigning
                // game end status (gameOver = true)
                winnerName = "Computer"
                resetBorderHighlight()
                showWinner(winnerName)
                gameOver = true
            } else if (checkDraw()) {
                // Function call resetBorderHighlight() to reset player layout border highlights,
                // function call showDraw() to display draw message and assigning
                // game end status (gameOver = true)
                resetBorderHighlight()
                showDraw()
                gameOver = true
            } else {
                // If the game is not over, then switch to the next player's turn
                currentPlayerTurn(if (playerTurn == 1) 2 else 1)
            }
            // Reset the computer thinking flag after making the move
            isComputerThinking = false
        }, 750) // Delay time (750 milliseconds) for the computer's move
    }

    // Function to update the current player's turn and highlight the player's layout border
    private fun currentPlayerTurn(curPlayerTurn: Int) {
        playerTurn = curPlayerTurn

        // Check the current player's turn and set the appropriate layout border
        if ((playerTurn == 1 && !isPVC) ||
            (isPVC && playerTurn == 1 && isPlayerSymbolX) ||
            (isPVC && playerTurn != 1 && !isPlayerSymbolX)) {
            playerLayoutX.setBackgroundResource(R.drawable.border_player_turn)
            playerLayoutO.setBackgroundResource(R.drawable.border)
        }
        else {
            playerLayoutX.setBackgroundResource(R.drawable.border)
            playerLayoutO.setBackgroundResource(R.drawable.border_player_turn)
        }
    }

    // Function to reset the border highlighting for player layouts
    private fun resetBorderHighlight() {
        playerLayoutO.setBackgroundResource(R.drawable.border)
        playerLayoutX.setBackgroundResource(R.drawable.border)
    }

    // Boolean function to check if the current player has won the game. Function returns true
    // if the current player wins, or returns false if no winning combination is found
    private fun checkWinner(): Boolean {
        // Go through each winning combination in winningCombinationsList
        for (combination in winningCombinationsList) {
            val (a, b, c) = combination
            // Check if the current player occupies all three positions in any winning combination
            if (boxPos[a] == playerTurn && boxPos[b] == playerTurn && boxPos[c] == playerTurn) {
                return true
            }
        }
        return false
    }

    // Function to display the winner of the game
    @SuppressLint("SetTextI18n")
    private fun showWinner(winnerName: String) {
        // Set the text to display the winner's name
        winnerTextView.text = "$winnerName has won!"
        // Show buttons to restart the game or return to the menu
        showButtons()
    }

    // Boolean function to check if the game result is a draw. Function will return true if
    // all the fields on the game board are occupied
    private fun checkDraw(): Boolean {
        // Check if all game board fields are occupied
        return boxPos.all { it != 0 }
    }

    // Function to display a message about the draw
    @SuppressLint("SetTextI18n")
    private fun showDraw() {
        // Set the text to display a draw
        winnerTextView.text = "It's a draw!"
        // Show buttons to restart the game or return to the menu
        showButtons()
    }

    // Function to display a message with a winner or a draw, as well as a menu and restart buttons
    private fun showButtons() {
        winnerTextView.visibility = View.VISIBLE
        menuButton.visibility = View.VISIBLE
        restartButton.visibility = View.VISIBLE
    }

    // Function to restart the game
    private fun restartGame() {

        boxPos = IntArray(9) // Reset selected game board field positions in array
        winnerName = "" // Reset winner name
        gameOver = false // Reset game over flag

        // Reset all game board field images to initial state with no filled symbols
        field1.setImageResource(R.drawable.back)
        field2.setImageResource(R.drawable.back)
        field3.setImageResource(R.drawable.back)
        field4.setImageResource(R.drawable.back)
        field5.setImageResource(R.drawable.back)
        field6.setImageResource(R.drawable.back)
        field7.setImageResource(R.drawable.back)
        field8.setImageResource(R.drawable.back)
        field9.setImageResource(R.drawable.back)

        winnerTextView.visibility = View.GONE // Hide the winner or draw message, restart and menu buttons
        menuButton.visibility = View.GONE
        restartButton.visibility = View.GONE

        playerTurn = if (!isPVC || isPlayerSymbolX) 1 else 2 // Set the player turn based on game mode
        currentPlayerTurn(playerTurn) // Highlight the current player's layout border

        // At the beginning of the game it is checked if it's the computer's turn in PVE mode,
        // then computer must make the first move
        if (isPVC && playerTurn == 2) {
            makeComputerMove()
        }
    }
}