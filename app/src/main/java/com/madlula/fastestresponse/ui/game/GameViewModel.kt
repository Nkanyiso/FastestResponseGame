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
    private var chosenColor = MutableLiveData<Event<Int>>()
    private var score =  MutableLiveData<Int>()
    private var arrowDirection = MutableLiveData<Event<Float>>()
    private var intervalTime: Long = 0L
    private var roundNumber: Int = 0
    private var gameStarted: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    private var nextArrow = MutableLiveData<Event<Int>>()
    private var gameFinished = MutableLiveData<Event<Boolean>>()
    private var lastOrientationPosition: Int = 0
    private var arrowShown: Boolean = false
    private var waitingToShowArrow: Boolean = false
    private var isTilted = false


    fun init(color : Int?) {
        score.value = 0
        color?.let { it ->
            chosenColor.value = Event(data = it)
        }

        gameStarted.value = Event(true)
        gameFinished.value = Event(false)
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
    fun getChosenColor(): LiveData<Event<Int>> = chosenColor
    fun getScore(): LiveData<Int> = score


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
        isTilted = false
        arrowDirection.value = Event(getRandomDirection().toFloat())
        arrowShown = true
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
                arrowShown = false
            }, Constants.DEFAULT_DELAY)

        }
    }

    fun detectedTilt(orientation: Int) {
        if (arrowDirection.value == null || gameFinished.value?.peek() == true || gameStarted.value?.peek() != true) {
            lastOrientationPosition = orientation
            return
        } else if (waitingToShowArrow && !isTilted) {
            score.value =  (score.value.toString().toInt() - 1)
            isTilted = true
            lastOrientationPosition = orientation
            return
        } else if (isTilted) {
            lastOrientationPosition = orientation
            return
        } else if (arrowShown) {
          //  score.value =  20
            if (arrowDirection.value!!.peek() == Utilities.isAcceptableTilt(lastOrientationPosition, orientation).toFloat()) {
                score.value =  (score.value.toString().toInt() + 1)
                isTilted = true
            }
            lastOrientationPosition = orientation
        } else {
            lastOrientationPosition = orientation
        }

    }

}