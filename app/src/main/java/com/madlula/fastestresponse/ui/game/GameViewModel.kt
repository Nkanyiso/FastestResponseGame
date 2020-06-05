package com.madlula.fastestresponse.ui.game

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Event
import com.madlula.fastestresponse.utilities.Utilities
import com.madlula.fastestresponse.utilities.Utilities.getRandomDirection
import com.madlula.fastestresponse.utilities.Utilities.getRandomInterval

class GameViewModel : ViewModel() {
    var chosenColor: MutableLiveData<Int> = MutableLiveData()
    var score: MutableLiveData<Int> = MutableLiveData<Int>()
    private var arrowDirection: MutableLiveData<Event<Float>> = MutableLiveData<Event<Float>>()
    private var intervalTime: Long = 0L
    private var roundNumber: Int = 0
    private var gameStarted: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private var nextArrow: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    private var gameFinished: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private var lastOrientationPosition: Int = 0


    var arrowShown: Boolean = false
    var waitingToShowArrow: Boolean = false
    var isTilted = false


    fun init() {
        score.value = 0
        gameStarted.value = Event(true)
        arrowShown = false
        waitingToShowArrow = false
        isTilted = false
        roundNumber = 0
        startNextRound()
    }

    fun getArrowDirection(): LiveData<Event<Float>> = arrowDirection
    fun isGameFinished(): LiveData<Event<Boolean>> = gameFinished
    fun isGameStarted(): LiveData<Event<Boolean>> = gameStarted
    fun getNextArrow(): LiveData<Event<Int>> = nextArrow


    private fun rotateArrow() {

        waitingToShowArrow = true
        isTilted = false

        if (roundNumber > 1) {
            Handler().postDelayed({
                doShowArrow()
            }, intervalTime)
        } else {
            doShowArrow()
        }

    }

    private fun doShowArrow() {
        waitingToShowArrow = false

        arrowDirection.value = Event(getRandomDirection().toFloat())
        isTilted = false
        startNextRound()
    }

    private fun startNextRound() {
        if (Constants.GAME_ROUND_MAX != roundNumber) {
            Handler().postDelayed({
                intervalTime = (getRandomInterval() * 1000).toLong()
                roundNumber++
                nextArrow.value = Event(roundNumber)
                Handler().postDelayed({
                    rotateArrow()
                }, Constants.DEFAULT_DELAY_TWO)//give user enough time to put device in default position after each round

            }, Constants.DEFAULT_DELAY)//only show arrow for 3 seconds
        } else {
            //game finished
            Handler().postDelayed({
                gameFinished.value = Event(true)
            }, Constants.DEFAULT_DELAY)

        }
    }

    fun detectedTilt(orientation: Int) {
        if (arrowDirection.value == null || gameFinished.value?.peek() ?: false || gameStarted.value?.peek() ?: false == false) {
            lastOrientationPosition = orientation
            return;
        } else if (waitingToShowArrow) {
            score.value = (score.value.toString().toInt() - 1)
            isTilted = true
            lastOrientationPosition = orientation
            return
        } else if (isTilted) {
            lastOrientationPosition = orientation
            return
        } else if (arrowShown) {
            if (arrowDirection.value!!.peek() == Utilities.isAcceptableTilt(lastOrientationPosition, orientation).toFloat()) {
                score.value = (score.value.toString().toInt() + 1)
                isTilted = true
            }
            lastOrientationPosition = orientation
        } else {
            lastOrientationPosition = orientation
        }

    }

}