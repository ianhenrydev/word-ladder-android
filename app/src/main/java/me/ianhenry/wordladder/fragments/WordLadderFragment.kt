package me.ianhenry.wordladder.fragments

import android.support.v4.app.Fragment

/**
 * Created by ianhe on 5/6/2017.
 */

abstract class WordLadderFragment : Fragment() {
    abstract fun onSpeechResult(results: Array<String>)
    abstract fun onDoubleTap()
}
