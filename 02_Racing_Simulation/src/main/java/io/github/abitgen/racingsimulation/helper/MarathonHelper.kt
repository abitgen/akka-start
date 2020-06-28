package io.github.abitgen.racingsimulation.helper

import akka.actor.typed.ActorRef
import io.github.abitgen.racingsimulation.RacerController.Companion.RacerCommand

class MarathonHelper(val totalRacers: Int) {

    private val start by lazy { System.currentTimeMillis() }
    private val displayLength = 160

    fun displayRace(currentRacerPositions: Map<ActorRef<RacerCommand>, Double>) {
        for (i in 0..49) println()
        println("Race has been running for " + ((System.currentTimeMillis() - start.toDouble()) / 1000) + " seconds.")
        println("    " + String(CharArray(displayLength)).replace('\u0000', '='))

        currentRacerPositions.keys.forEach {
            println(
                "${it.path().name()} : " + String(CharArray(currentRacerPositions.getOrElse(it) { 0.0 }.toInt() * displayLength / 100)).replace(
                    '\u0000',
                    '*'
                ) + " ${currentRacerPositions[it]!!.toInt()}"
            )
        }
    }

    fun printRaceResults(finishedTimeMap: Map<ActorRef<RacerCommand>, Long>) {
        println("Results")

        finishedTimeMap.entries.sortedWith(kotlin.Comparator { o1, o2 ->
            o1.value.compareTo(o2.value)
        }).forEach {
            println("Racer " + it.key.path().name() + " finished in " + (it.value - start.toDouble()) / 1000 + " seconds.")
        }
    }
}