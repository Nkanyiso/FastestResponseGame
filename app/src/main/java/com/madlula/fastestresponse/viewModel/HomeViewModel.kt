package com.madlula.fastestresponse.viewModel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.madlula.fastestresponse.utilities.Constants
import com.madlula.fastestresponse.utilities.Event

class HomeViewModel : ViewModel() {
    var appInitialized: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    var chosenColor: MutableLiveData<Int> = MutableLiveData()
    var countDown: MutableLiveData<Event<Long>> = MutableLiveData<Event<Long>>()
    var countDownFinished: MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
    fun isAppInitialized(): LiveData<Event<Boolean>> = appInitialized
    fun isCountDownFinished(): LiveData<Event<Boolean>> = countDownFinished
    fun getcountDown():  LiveData<Event<Long>> = countDown
    fun init() {
        appInitialized.value = Event(true)
    }
    fun setChosenColor(color :Int){
        chosenColor.value = color
        doCountDown()
    }
    fun doCountDown() {
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