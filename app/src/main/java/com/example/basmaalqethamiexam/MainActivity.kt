package com.example.basmaalqethamiexam

import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var myRV: RecyclerView
    private lateinit var submitButton: Button
    private lateinit var answerTextField: EditText

    private lateinit var textViewHighScore: TextView
    private lateinit var textViewScore: TextView
    private lateinit var textViewQuations: TextView

    private lateinit var clMain: ConstraintLayout

    private val answers = ArrayList<String>()

    private var firstRandomNumber = 0
    private var secondRandomNumber = 0
    private var correctAnswer = 0
    var difficultEquationAdd = 5
    var difficultEquationSub = 5
    var difficultEquationMult = 5
    var op = "+"
    var showalert = true
    private lateinit var sharedPreferences: SharedPreferences
    private var highScore = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        showalert = sharedPreferences.getBoolean("showalert", true)

        if (showalert) {
            startAlertDialog()
        }

        sharedPreferences = this.getSharedPreferences(
            getString(R.string.preference_file_key), Context.MODE_PRIVATE)
        highScore = sharedPreferences.getInt("HighScore", 0)
        textViewHighScore = findViewById(R.id.highScore)
        textViewHighScore.text = "High Score: $highScore"


        myRV = findViewById(R.id.recyclerView)
        myRV.adapter = RowRecyclerView(answers)
        myRV.layoutManager = LinearLayoutManager(this)

        submitButton = findViewById(R.id.submitButton)
        answerTextField = findViewById(R.id.editAnswer)
        textViewScore = findViewById(R.id.score)
        textViewQuations = findViewById(R.id.quations)
        clMain = findViewById(R.id.clMain)

        //app generate random equations
        generateProblem()
        submitButton.setOnClickListener { AddTOList() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.addition -> {
                op = "+"
                generateProblem ()
                return true
            }
            R.id.subtraction -> {
                op = "-"
                generateProblem ()
                return true
            }
            R.id.multiplication -> {
                op = "*"
                generateProblem ()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun generateProblem (){

        /* The problems should get progressively more difficult
           with each equation (use larger numbers) */

        /* If the user answers all the questions and his score is 5,
           the difficulty of the questions will increase,
           and after he gets score 15 it will become more difficult than before,
           and so on until he gets the highest difficulty score.


        if(score < 5) {
            firstRandomNumber = Random.nextInt(1, 10)
            secondRandomNumber = Random.nextInt(0, 10)
        } else if (score < 15) {
            firstRandomNumber = Random.nextInt(1, 25)
            secondRandomNumber = Random.nextInt(0, 25)
        } else if (score < 25) {
            firstRandomNumber = Random.nextInt(1, 100)
            secondRandomNumber = Random.nextInt(0, 100)
        } else {
            firstRandomNumber= Random.nextInt(1, 1000)
            secondRandomNumber= Random.nextInt(0, 1000)
        }
         */

        if (op == "+"){
            difficultEquationAdd += 4
            firstRandomNumber= Random.nextInt(1, difficultEquationAdd)
            secondRandomNumber= Random.nextInt(0, difficultEquationAdd)
            correctAnswer= firstRandomNumber + secondRandomNumber

        } else if (op == "-") {
            difficultEquationSub += 4
            firstRandomNumber= Random.nextInt(1, difficultEquationSub)
            secondRandomNumber= Random.nextInt(0, difficultEquationSub)
            correctAnswer= firstRandomNumber - secondRandomNumber
        } else if (op == "*") {
            difficultEquationMult += 4
            firstRandomNumber= Random.nextInt(1, difficultEquationMult)
            secondRandomNumber= Random.nextInt(0, difficultEquationMult)
            correctAnswer= firstRandomNumber * secondRandomNumber
        }
        textViewQuations.text = "$firstRandomNumber $op $secondRandomNumber ="
    }

    private fun AddTOList() {
        var answer = answerTextField.text.toString()

        if (answer.isNotEmpty()) {
            try {
                if (answer.toInt() == correctAnswer){
                    //user enters the correct solution, the app generates another problem
                    //and adds one point to their score.
                    answers.add("$firstRandomNumber $op $secondRandomNumber = $correctAnswer")
                    score++
                    textViewScore.text = "Score: $score"
                    generateProblem ()
                } else {
                    //Otherwise, the round ends
                    answers.add("-----------------------------------------")
                    answers.add("The correct answer was \n$firstRandomNumber $op $secondRandomNumber = $correctAnswer.")
                    updateScore() // update high score
                    disableEntry() //Disable the 'Submit' button when the round ends
                    //Allow users to start a new round
                    showAlertDialog("Play again?")
                }

                myRV.scrollToPosition(answers.size - 1)
                answerTextField.text.clear()
                myRV.adapter?.notifyDataSetChanged()
            } catch (e: Exception) {
                Snackbar.make(clMain, "Please enter number only", Snackbar.LENGTH_LONG).show()
            }
        } else {
            Snackbar.make(clMain, "Please enter number", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateScore(){
        if(score >= highScore){
            highScore = score
            with(sharedPreferences.edit()) {
                putInt("HighScore", highScore)
                apply()
            }
            textViewHighScore.text = "High Score: $highScore"
            Snackbar.make(clMain, "NEW HIGH SCORE!", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun disableEntry() {
        submitButton.isEnabled = false
        submitButton.isClickable = false
    }

    private fun showAlertDialog(title: String) {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage(title)
            .setCancelable(false)
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->
                showalert = false
                with(sharedPreferences.edit()) {
                    putBoolean("showalert", showalert)
                    apply()
                }
                this.recreate() //Allow users to start a new round
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                showalert = true
                with(sharedPreferences.edit()) {
                    putBoolean("showalert", showalert)
                    apply()
                }
                dialog.cancel()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Math Study App")
        alert.show()
    }

    private fun startAlertDialog() {
        val dialogBuilder = AlertDialog.Builder(this)

        dialogBuilder.setMessage("Welcome to the Math Study App!\nHow many equations can you solve?")
            .setCancelable(false)
            .setPositiveButton("START", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })

        val alert = dialogBuilder.create()
        alert.setTitle("Math Study App")
        alert.show()
    }
}