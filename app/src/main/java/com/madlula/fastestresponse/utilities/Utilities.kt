package com.madlula.fastestresponse.utilities

import com.madlula.fastestresponse.R
import kotlin.math.abs
import kotlin.random.Random

object Utilities {
    open fun getColor(chosenColor: Int?): Int {
        when (chosenColor) {
            0 -> return R.color.Red
            1 -> return R.color.Blue
            2 -> return R.color.Green
            3 -> return R.color.Pink
            else -> return R.color.Red
        }
    }

    fun getRandomInterval(): Int = Random.nextInt(Constants.INTERVAL_MINIMUM_VALUE, Constants.INTERVAL_MAX_VALUE)
    fun getRandomDirection(): Int {
        var pos = Random.nextInt(Constants.GAME_TILT_DIRECTION_MIN, Constants.GAME_TILT_DIRECTION_MAX)
        return Constants.ARROW_ANGLES[pos]
    }

    fun isAcceptableTilt(previousTilt: Int, newTilt: Int): Int {
        return if (abs(newTilt - previousTilt) >= Constants.GAME_TILT_VARIANT) {
            when (newTilt) {
                in 0..115 -> 0//forward
                in 155..205 -> 270//backward
                in 245..295 -> 180//left
                in 335..360 -> 90//right
                else -> -1
            }
        } else {
            -1
        }
    }

}