package me.ianhenry.wordladder.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.database.SQLException
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import java.io.IOException
import java.util.ArrayList

import me.ianhenry.wordladder.MainActivity
import me.ianhenry.wordladder.R
import me.ianhenry.wordladder.tools.WordDatabaseHelper

/**
 * Created by ianhe on 5/6/2017.
 */

class GameFragment : WordLadderFragment() {
    private var mainActivity: MainActivity? = null
    private var wordDatabaseHelper: WordDatabaseHelper? = null
    private var alreadyAnswered: ArrayList<String>? = null
    private var solutions: ArrayList<String>? = null
    private var score = 0
    private var promptText: TextView? = null
    private var timeText: TextView? = null
    private var scoreText: TextView? = null
    private var previousText: TextView? = null
    private var currentWord: String? = null
    private val gameTime = 60
    private var difficulty: Int = 0

    private val gameTimer = object : CountDownTimer((gameTime * 1000).toLong(), 1000) {
        override fun onTick(millisUntilFinished: Long) {
            timeText!!.setText((millisUntilFinished / 1000).toString() + "")
            if (millisUntilFinished <= 10000) {
                mainActivity!!.playBackgroundSound("tick")
            }
        }

        override fun onFinish() {
            mainActivity!!.playSound("GameOver", null)
            val sp = getContext()!!.getSharedPreferences("prefs", Activity.MODE_PRIVATE)
            mainActivity!!.speak(score.toString() + " points")
            if (score > sp.getInt("high_score", 0)) {
                sp.edit().putInt("high_score", score).apply()
                mainActivity!!.playSound("MadeLeaderboard", null)
            }
            mainActivity!!.returnToMenu()
        }
    }

    override fun onAttach(context: Context?) {
        mainActivity = context as MainActivity?
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_game, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        promptText = getView()!!.findViewById(R.id.prompt)
        timeText = getView()!!.findViewById(R.id.time)
        scoreText = getView()!!.findViewById(R.id.score)
        previousText = getView()!!.findViewById(R.id.previous)

        difficulty = getArguments()!!.getInt("difficulty", 3)

        initDB()
        mainActivity!!.playSound("YourFirstWord", null)
        alreadyAnswered = ArrayList()
        newWord()
        gameTimer.start()
    }

    private fun initDB() {
        wordDatabaseHelper = WordDatabaseHelper(context)
        try {
            wordDatabaseHelper!!.createDataBase()
        } catch (ioe: IOException) {
            throw Error("Unable to create database")
        }

        try {
            wordDatabaseHelper!!.openDataBase()
        } catch (sqle: SQLException) {
            throw sqle
        }

    }

    private fun newWord() {
        val randoCursor = wordDatabaseHelper!!.getRandoWord(difficulty)
        randoCursor.moveToFirst()
        val randoWord = randoCursor.getString(0)
        promptText!!.setText(randoWord.toUpperCase())
        getSolutions(randoWord)
        mainActivity!!.speakPrompt(randoWord)
        alreadyAnswered!!.add(randoWord.toUpperCase())
        currentWord = randoWord
    }

    private fun getSolutions(word: String) {
        solutions = ArrayList()
        val cursor = wordDatabaseHelper!!.getSolutions(word)
        while (cursor.moveToNext()) {
            solutions!!.add(cursor.getString(0))
        }
    }

    override fun onSpeechResult(results: Array<String>) {
        var correct = false
        outerloop@ for (result in results) {
            for (solution in solutions!!) {
                if (result.toUpperCase() == solution.toUpperCase()) {
                    if (!alreadyAnswered!!.contains(result.toUpperCase())) {
                        alreadyAnswered!!.add(result.toUpperCase())
                        Log.d("Correct", result)
                        promptText!!.setText(result.toUpperCase())
                        mainActivity!!.playSound("Correct", null)
                        correct = true
                        previousText!!.setText(currentWord!!.toUpperCase() + "\n" + previousText!!.getText())
                        currentWord = result
                        increaseScore()
                        getSolutions(result)
                        break@outerloop
                    }
                }
            }
        }
        if (!correct)
            mainActivity!!.playSound("Incorrect", null)
    }

    private fun increaseScore() {
        score++
        scoreText!!.setText(score.toString() + "")
    }

    override fun onDoubleTap() {
        mainActivity!!.speakPrompt(currentWord!!)
    }
}
