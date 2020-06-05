package com.madlula.fastestresponse.ui.home

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Event

class HomeViewModel : ViewModel() {
    private var appInitialized = MutableLiveData<Event<Boolean>>()
     var chosenColor = MutableLiveData<Int>()
    private var countDown = MutableLiveData<Event<Long>>()
    var countDownFinished = MutableLiveData<Event<Boolean>>()
    fun isAppInitialized(): LiveData<Event<Boolean>> = appInitialized
    fun goStartGame(): LiveData<Event<Boolean>> = countDownFinished
    fun getCountDown():  LiveData<Event<Long>> = countDown
    fun init() {
        appInitialized.value = Event(true)
    }
    fun setChosenColor(color :Int){
        chosenColor.value = color
        doCountDown()
    }
    private fun doCountDown() {
        object : CountDownTimer(Constants.COUNT_DOWN_MAX_VALUE, Constants.COUNT_DOWN_INTERVAL) {
            override fun onTick(millisUntilFinished: Long) {
                countDown.value = Event(millisUntilFinished)
            }

            override fun onFinish() {
                countDownFinished.value = Event(true)
            }
        }.start()

    }
}