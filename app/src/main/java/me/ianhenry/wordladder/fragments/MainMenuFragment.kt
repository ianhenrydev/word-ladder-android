package me.ianhenry.wordladder.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.ianhenry.wordladder.MainActivity
import me.ianhenry.wordladder.R

/**
 * Created by ianhe on 5/6/2017.
 */

class MainMenuFragment : WordLadderFragment() {

    private var mainActivity: MainActivity? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity!!.playSound("Welcome", null)
    }

    override fun onSpeechResult(results: Array<String>) {
        for (result in results) {
            when (result.toUpperCase()) {
                PLAY -> mainActivity!!.chooseMode()
                TUTORIAL -> mainActivity!!.playSound("TutorialReadOut", null)
                LEADERBOARD -> {
                    mainActivity!!.playSound("1stPlace", null)
                    val sp = getContext()!!.getSharedPreferences("prefs", Activity.MODE_PRIVATE)
                    mainActivity!!.speak(sp.getInt("high_score", 0).toString() + " points")
                }
            }
        }
    }

    override fun onDoubleTap() {
        mainActivity!!.playSound("Welcome", null)
    }

    companion object {
        private val PLAY = "PLAY"
        private val LEADERBOARD = "LEADERBOARD"
        private val TUTORIAL = "TUTORIAL"
    }
}
