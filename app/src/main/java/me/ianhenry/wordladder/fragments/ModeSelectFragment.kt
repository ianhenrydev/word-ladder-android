package me.ianhenry.wordladder.fragments

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import me.ianhenry.wordladder.MainActivity
import me.ianhenry.wordladder.R

/**
 * Created by ianhe on 5/7/2017.
 */

class ModeSelectFragment : WordLadderFragment() {

    private var mainActivity: MainActivity? = null
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mainActivity = context as MainActivity?
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_mode, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainActivity!!.playSound("SelectDifficulty", null)
    }

    override fun onSpeechResult(results: Array<String>) {
        for (result in results) {
            when (result.toUpperCase()) {
                "EASY" -> mainActivity!!.startGame(3)
                "MEDIUM" -> mainActivity!!.startGame(4)
                "HARD" -> mainActivity!!.startGame(5)
            }
        }
    }

    override fun onDoubleTap() {
        mainActivity!!.playSound("SelectDifficulty", null)
    }
}
