package com.madlula.fastestresponse.viewModel

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Event
import com.madlula.fastestresponse.utilities.Utilities.getRandomDirection
import com.madlula.fastestresponse.utilities.Utilities.getRandomInterval

class GameViewModel : ViewModel() {
    var chosenColor: MutableLiveData<Int> = MutableLiveData()
    var score: MutableLiveData<Int> = MutableLiveData<Int>()
    var arrowDirection: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    var intervalTime: Long = 0L
    var roundNumber : Int = 0
    var gameStarted : MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var nextArrow : MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    var gameFinished: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var prevArrowDirection = 0;
    var lastPosition : Int = 0

    var arrowShown: Boolean = false
    var waitingToShowArrow: Boolean = false
    var isTilted = false;


    fun init() {
        score.value = 0
        gameStarted.value = Event(true)
        startNextRound()
    }

    fun getArrowDirection(): LiveData<Event<Int>> = arrowDirection
    fun isGameFinished(): LiveData<Event<Boolean>> = gameFinished
    fun isgameStarted():  LiveData<Event<Boolean>> = gameStarted
    fun getNextArrow():  LiveData<Event<Int>> = nextArrow


    private fun rotateArrow(){
        waitingToShowArrow = true
        isTilted = false

        if(roundNumber > 1){
            Handler().postDelayed({
                doShowArrow()
            }, intervalTime )
        }else{
            doShowArrow()
        }

    }
    private fun doShowArrow(){
        waitingToShowArrow = false
        arrowDirection.value = Event(getRandomDirection())
        startNextRound()
    }
    private fun startNextRound(){
        if (Constants.GAME_ROUND_MAX != roundNumber  ) {
            Handler().postDelayed({
                intervalTime = (getRandomInterval() * 1000).toLong()
                roundNumber ++
                nextArrow.value = Event(roundNumber)
                rotateArrow()
            }, 3000)//only show arrow for 3 seconds
        } else {
            //game finished
            Handler().postDelayed({
            gameFinished.value = Event(true)
            }, 3000)

        }
    }
    fun detectedTilt(orientation: Int) {
        if (waitingToShowArrow) {
            (score.value.toString().toInt() - 1)
            return
        } else if (isTilted) {
            return
        } else if (arrowShown) {
            when (orientation) {
                in 1..120 -> {// up
                    if (arrowDirection.value == Event(1)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }

                in 320..360 -> {//right
                    if (arrowDirection.value == Event(2)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 240..290 -> {//Down
                    if (arrowDirection.value == Event(3)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 150..200 -> {//left
                    if (arrowDirection.value == Event(4)) {
                        score.value = (score.value.toString().toInt() + 1)
                    }

                }
            }
        }

    }

}