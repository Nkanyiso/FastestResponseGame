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
    var arrowDirection: MutableLiveData<Event<Float>> = MutableLiveData<Event<Float>>()
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
        arrowShown = false
        waitingToShowArrow = false
        isTilted = false
        roundNumber = 0
        startNextRound()
    }

    fun getArrowDirection(): LiveData<Event<Float>> = arrowDirection
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
        arrowDirection.value =  Event(getRandomDirection().toFloat())
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
        if(arrowDirection.value == null){
            return;
        }
//        if (waitingToShowArrow) {
//            (score.value.toString().toInt() - 1)
//            return
//        } else if (isTilted) {
//            return
//        } else if (arrowShown) {
            when (orientation) {
                in 0..25 -> {// up
                    if (arrowDirection.value!!.peek() == 0F) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }

                in 65..115 -> {//right
                    if (arrowDirection.value!!.peek() == 0F) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 155..205 -> {//Down
                    if (arrowDirection.value!!.peek() == 270F) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 245..290 -> {//left
                    if (arrowDirection.value!!.peek() == 180F) {
                        score.value = (score.value.toString().toInt() + 1)
                    }
                }
                in 335..360 -> {//left
                    if (arrowDirection.value!!.peek() == 90F) {
                        score.value = (score.value.toString().toInt() + 1)
                    }

                }
//            }
        }

    }

}