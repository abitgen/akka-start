package io.github.abitgen.racingsimulation.logic

import kotlin.random.Random

class MockRacerAlgo(override val raceLength:Int) : IsRacing{

    private var currentSpeed = 0.0
    private var currentPosition = 0.0
    private val defaultAverageSpeed = 48.2 // in km/h
    private val random: Random = Random.Default
    private val averageSpeedAdjustmentFactor: Int by lazy {  random.nextInt(30) - 10 }

    private fun getDistanceMovedPerSecond(): Double {
        return currentSpeed * 1000 / 3600
    }

    private fun getMaxSpeed(): Double {
        return defaultAverageSpeed * (1 + averageSpeedAdjustmentFactor/ 100)
    }

    override fun updateCurrentSpeed() {
        currentSpeed = if (currentPosition < raceLength / 4) {
            currentSpeed + (getMaxSpeed() - currentSpeed) / 10 * random.nextDouble()
        } else {
            currentSpeed * (0.5 + random.nextDouble())
        }

        if (currentSpeed > getMaxSpeed()) currentSpeed = getMaxSpeed()
        if (currentSpeed < 5) currentSpeed = 5.0

        if (currentPosition > raceLength / 2 && currentSpeed < getMaxSpeed() / 2) {
            currentSpeed = getMaxSpeed() / 2
        }
    }

    override fun updateCurrentPosition() : Double{

        currentPosition = if (currentPosition+ getDistanceMovedPerSecond() >= raceLength)
                            raceLength.toDouble()
                          else
                            currentPosition + getDistanceMovedPerSecond()
        return currentPosition
    }

    override fun hasCompleted() : Boolean = currentPosition.toInt() == raceLength

}

