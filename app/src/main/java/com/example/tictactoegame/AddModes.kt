// This game was created by Deniss Bondars, 221RDB122

// Most of the information about how to design the game using drawable resource files (.xml),
// switch different windows, as well as the logic of creating the Tic Tac Toe game for PVP mode
// was taken from this video in which a Tic Tac Toe game was created in Java:
// https://www.youtube.com/watch?v=9hOJH7IflFo&t=585s
// Also, to simplify game development and to understand the correct syntax and learn commands in
// the Kotlin language, artificial intelligence "ChatGPT" was used for some code fragments,
// mostly for PVC mode realization.

// Link to GitHub project:

package com.example.tictactoegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast

class AddModes : AppCompatActivity() {

    private lateinit var player1EditText: EditText // EditText for entering player names in PVP mode
    private lateinit var player2EditText: EditText

    private lateinit var playerNamePVC: EditText // EditText for entering player name in PVC mode

    private lateinit var startGameBtn: Button // Button to start the game

    private lateinit var radioGroupMode: RadioGroup // RadioGroup for selecting game mode
    private lateinit var radioGroupSymbol: RadioGroup // RadioGroup for player's selected symbol

    private lateinit var layoutPVP: LinearLayout // LinearLayout represent the layout for PVP and PVC mode options
    private lateinit var layoutPVC: LinearLayout

    private var playerXName = "" // String variables to store the names of players with X and with O in PVP mode
    private var playerOName = ""

    private var playerName = "" // String variable to store the name of player in PVC mode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_modes)

        // Welcome message when the game or activity is launched
        Toast.makeText(this, "Welcome to Tic Tac Toe game created by Deniss Bondars!", Toast.LENGTH_LONG).show()

        // Initialize fields and find elements by their ID
        player1EditText = findViewById(R.id.playerNamePvp1)
        player2EditText = findViewById(R.id.playerNamePvp2)
        playerNamePVC = findViewById(R.id.playerNamePVC)
        startGameBtn = findViewById(R.id.btnStartGame)
        radioGroupMode = findViewById(R.id.radioGroupMode)
        radioGroupSymbol = findViewById(R.id.radioGroupSymbol)
        layoutPVP = findViewById(R.id.layoutPVP)
        layoutPVC = findViewById(R.id.layoutPVC)

        // RadioGroup listener for selecting PVP and PVE game mode where,
        // settings appear depending on the current mode
        radioGroupMode.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioButtonPVP -> {
                    layoutPVP.visibility = View.VISIBLE
                    layoutPVC.visibility = View.GONE
                    startGameBtn.visibility = View.VISIBLE
                }
                R.id.radioButtonPVC -> {
                    layoutPVP.visibility = View.GONE
                    layoutPVC.visibility = View.VISIBLE
                    startGameBtn.visibility = View.VISIBLE
                }
            }
        }

        // Button click listener for "Start Game" button
        startGameBtn.setOnClickListener {

            if (radioGroupMode.checkedRadioButtonId == R.id.radioButtonPVP) {
                // Check if "Player Vs Player" mode is selected
                playerXName = player1EditText.text.toString()
                playerOName = player2EditText.text.toString()

                if (playerXName.isEmpty() || playerOName.isEmpty()) {
                    // Check if player name fields are empty
                    Toast.makeText(this, "Please enter player names", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                else {
                    // Pass data to MainActivity and start Tic Tac Toe game
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("playerXName", playerXName)
                    intent.putExtra("playerOName", playerOName)
                    intent.putExtra("isPVC", false)
                    startActivity(intent)
                }
            }

            else if (radioGroupMode.checkedRadioButtonId == R.id.radioButtonPVC) {
                // Check if "Player Vs Computer" mode is selected
                playerName = playerNamePVC.text.toString()
                val checkSelectedSymbol = radioGroupSymbol.checkedRadioButtonId

                if (playerName.isEmpty()) {
                    // Check if player name field is empty
                    Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }


                if (checkSelectedSymbol == -1) {
                    // Check if player symbol choice is empty
                    Toast.makeText(this, "Please choose your symbol", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // here the symbol chosen by the player is determined and, depending on this,
                // it will be determined which symbol the computer will play for
                else {
                    val selectedSymbol = findViewById<RadioButton>(radioGroupSymbol.checkedRadioButtonId)
                    val isPlayerSymbolX = selectedSymbol.id == R.id.radioBtnX

                    if (isPlayerSymbolX) {
                        playerXName = playerName
                        playerOName = "Computer"
                    }
                    else {
                        playerXName = "Computer"
                        playerOName = playerName
                    }

                    // Pass data to MainActivity and start Tic Tac Toe game
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("playerXName", playerXName)
                    intent.putExtra("playerOName", playerOName)
                    intent.putExtra("isPVC", true)
                    intent.putExtra("isPlayerSymbolX", isPlayerSymbolX)
                    startActivity(intent)
                }
            }
        }
    }
}