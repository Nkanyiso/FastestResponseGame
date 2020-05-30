package com.madlula.fastestresponse.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

 class BaseViewModel : ViewModel(){
     var choosenColor : MutableLiveData<Int> = MutableLiveData()
     var score : MutableLiveData<Int> = MutableLiveData()
     var appInitialized : MutableLiveData<Event<Boolean>> = MutableLiveData<Event<Boolean>>()
     var tiltDirection : MutableLiveData<Event<Int>> = MutableLiveData<Event<Int>>()


     fun isAppInitialized(): LiveData<Event<Boolean>> = appInitialized
     fun init(){
         appInitialized.value = Event(true)
     }
     fun getArrowDirection(): LiveData<Event<Int>> = tiltDirection

     fun generateDirection(){
         var dir =(1..4).random()
         tiltDirection.value = Event(dir)
     }
 }