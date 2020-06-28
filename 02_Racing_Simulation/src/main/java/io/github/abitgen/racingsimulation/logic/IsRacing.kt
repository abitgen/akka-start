package io.github.abitgen.racingsimulation.logic

interface IsRacing{
    val raceLength:Int
    fun updateCurrentSpeed()
    fun updateCurrentPosition() : Double
    fun hasCompleted(): Boolean
}