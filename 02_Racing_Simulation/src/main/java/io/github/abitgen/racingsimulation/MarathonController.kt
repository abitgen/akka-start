package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.*
import io.github.abitgen.racingsimulation.Consts.MessageConstants.POLL_TIME
import io.github.abitgen.racingsimulation.Consts.MessageConstants.START_RACE
import io.github.abitgen.racingsimulation.Consts.MessageConstants.TIMER_TAG
import io.github.abitgen.racingsimulation.RacerController.Companion.PositionCommand
import io.github.abitgen.racingsimulation.RacerController.Companion.RacerCommand
import io.github.abitgen.racingsimulation.helper.MarathonHelper
import java.io.Serializable
import io.github.abitgen.racingsimulation.MarathonController.Companion.MarathonCommand as MarathonCommand
import io.github.abitgen.racingsimulation.RacerController.Companion.RacerStartCommand
import io.github.abitgen.racingsimulation.logic.MockRacerAlgo
import java.time.Duration

class MarathonController private constructor(context: ActorContext<MarathonCommand>, marathonHelper: ()->MarathonHelper) :
    AbstractBehavior<MarathonCommand>(context) {

    private val currentPositionsMap: HashMap<ActorRef<RacerCommand>, Double> = hashMapOf()
    private val finishedTimeMap: HashMap<ActorRef<RacerCommand>, Long> = hashMapOf()
    private val marathonUtil by lazy { marathonHelper.invoke() }

    companion object {
        interface MarathonCommand : Serializable

        data class StartCommand(val msg: String) : MarathonCommand {
            val serialVersionUID = 1L;
        }

        fun create(marathonHelper: () -> MarathonHelper): Behavior<MarathonCommand>? {
            return Behaviors.setup<MarathonCommand> { MarathonController(it, marathonHelper) }
        }

        private class GetPositionsCommand() : MarathonCommand {
            val serialVersionUID = 1L;
        }

        data class CompleteCommand(val time: Long, val racer: ActorRef<RacerCommand>) : MarathonCommand{
            val serialVersionUID = 1L;
        }

        data class UpdatePositionsCommand(val currentPosition: Double, val racer: ActorRef<RacerCommand>) : MarathonCommand{
            val serialVersionUID = 1L;
        }
    }

    override fun createReceive(): Receive<MarathonCommand>? {
        return startRaceHandler()
    }

    private fun startRaceHandler(): Receive<MarathonCommand>? {
        val mockRacer = { MockRacerAlgo(100) }

        return newReceiveBuilder().onMessage(StartCommand::class.java) {
            // Telling all Racers to start
            for (i in 0 until marathonUtil.totalRacers) {
                RacerController.create(mockRacer).apply {
                    context.spawn(this, "Racer_${i + 1}").run {
                        currentPositionsMap[this] = 0.0
                        tell(RacerStartCommand(START_RACE))
                    }
                }
            }

            // The controller then tells itself to poll for racer position every one second
            return@onMessage pollRacerPositionHandler()

        }.onMessage(GetPositionsCommand::class.java) {
            // On every poll the execution will come here, and telling each racer to provide the position/ distance covered
            currentPositionsMap.keys.forEach {
                it.tell(PositionCommand(context.self))
            }
            this
        }.onMessage(UpdatePositionsCommand::class.java){
            currentPositionsMap[it.racer] = it.currentPosition
            marathonUtil.displayRace(currentPositionsMap)
            this
        }.onMessage(CompleteCommand::class.java){
            finishedTimeMap.putIfAbsent(it.racer, it.time)
            if(finishedTimeMap.size != marathonUtil.totalRacers)
                Behaviors.same()
            else{
                marathonUtil.printRaceResults(finishedTimeMap)
                return@onMessage Behaviors.withTimers {
                    it.cancelAll()
                    Behaviors.stopped<MarathonCommand>()
                }
            }
        }.build()
    }

    private fun pollRacerPositionHandler(): Behavior<MarathonCommand>? {
        return Behaviors.withTimers<MarathonCommand> {
            it.startTimerAtFixedRate(TIMER_TAG, GetPositionsCommand(), Duration.ofMillis(POLL_TIME))
            this
        }
    }

    private fun onRaceComplete(): Receive<MarathonCommand>? {
        return newReceiveBuilder().
            onMessage(CompleteCommand::class.java){
                marathonUtil.printRaceResults(finishedTimeMap)
                return@onMessage Behaviors.withTimers {
                    it.cancelAll()
                    Behaviors.stopped<MarathonCommand>()
                }
            }.build()
    }
}

