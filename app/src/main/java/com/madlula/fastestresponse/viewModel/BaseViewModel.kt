package com.madlula.fastestresponse.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.madlula.fastestresponse.R

class BaseViewModel : ViewModel() {
    var choosenColor: MutableLiveData<Int> = MutableLiveData()
    var score: MutableLiveData<Int> = MutableLiveData<Int>()
    var appInitialized: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var tiltDirection: MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()
    var intervalTime: MutableLiveData<Event<Long>> = MutableLiveData<Event<Long>>()
    var arrowShown: Boolean = false
    var waitingToShowArrow: Boolean = false
    var isTilted = false;


    fun isAppInitialized(): LiveData<Event<Boolean>> = appInitialized
    fun init() {
        appInitialized.value = Event(true)
        score.value = 0
    }

    fun getArrowDirection(): LiveData<Event<Int>> = tiltDirection
    fun getIntervalTime(): LiveData<Event<Long>> = intervalTime
    //fun getScore(): LiveData<Event<Int>> = score

    fun generateDirection() {
        var dir = (1..4).random()
        tiltDirection.value = Event(dir)
    }

    fun generateInterval() {
        var interval = (2..5).random()
        intervalTime.value = Event((interval * 1000).toLong())
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
                    if (tiltDirection.value == Event(1)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }

                in 320..360 -> {//right
                    if (tiltDirection.value == Event(2)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 240..290 -> {//Down
                    if (tiltDirection.value == Event(3)) {
                        score.value = (score.value.toString().toInt() + 1).toInt()
                    }
                }
                in 150..200 -> {//left
                    if (tiltDirection.value == Event(4)) {
                        score.value = (score.value.toString().toInt() + 1)
                    }

                }
            }
        }

    }
    fun getColor() :Int{
        if(choosenColor.value==0){

        }
        when(choosenColor.value) {
            0 -> return R.color.Red
            1 -> return R.color.Green
            2 -> return R.color.Pink
            3 -> return R.color.Blue
            else -> return R.color.Red
        }
    }
}