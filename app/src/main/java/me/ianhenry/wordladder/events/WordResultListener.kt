package me.ianhenry.wordladder.events

/**
 * Created by ianhe on 2/22/2017.
 */

interface WordResultListener {
    fun onWordResult(words: Array<String>)
    fun onError()
}
