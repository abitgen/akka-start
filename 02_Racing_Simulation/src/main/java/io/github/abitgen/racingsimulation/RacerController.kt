package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.PostStop
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import io.github.abitgen.racingsimulation.MarathonController.Companion.MarathonCommand
import io.github.abitgen.racingsimulation.logic.IsRacing
import java.io.Serializable
import io.github.abitgen.racingsimulation.RacerController.Companion.RacerCommand as RacerCommand

class RacerController(context: ActorContext<RacerCommand>, val isRacing: () -> IsRacing) : AbstractBehavior<RacerCommand>(context) {

    lateinit var racer: IsRacing

    companion object{
        interface RacerCommand : Serializable

        data class RacerStartCommand(val msg:String) : RacerCommand{
            val serialVersionUID = 1L;
        }

        data class PositionCommand(val marathonController: ActorRef<MarathonCommand>) : RacerCommand{
            val serialVersionUID = 1L;
        }

        fun create(isRacing: () -> IsRacing): Behavior<RacerCommand>? {
            return Behaviors.setup<RacerCommand> { RacerController(it, isRacing) }
        }
    }

    override fun createReceive(): Receive<RacerCommand>? {
        return notYetStarted()
    }

    private fun notYetStarted() : Receive<RacerCommand>?{
        return newReceiveBuilder().onMessage(RacerStartCommand::class.java){
            racer = isRacing()
            this
        }.onMessage(PositionCommand::class.java){
            racer.updateCurrentSpeed()
            it.marathonController.tell(MarathonController.Companion.UpdatePositionsCommand(racer.updateCurrentPosition(), context.self))
            return@onMessage running()
        }.build()
    }

    private fun running(): Receive<RacerCommand>?{
        return newReceiveBuilder().onMessage(PositionCommand::class.java){
            if (racer.hasCompleted()){
                complete()
            }else {
                racer.updateCurrentSpeed()
                it.marathonController.tell(MarathonController.Companion.UpdatePositionsCommand(racer.updateCurrentPosition(), context.self))
                Behaviors.same()
            }
        }.build()
    }

    private fun complete(): Receive<RacerCommand>?{
        return newReceiveBuilder().onMessage(PositionCommand::class.java){
            //it.marathonController.tell(MarathonController.Companion.UpdatePositionsCommand(racer.raceLength.toDouble(), context.self))
            it.marathonController.tell(MarathonController.Companion.CompleteCommand(System.currentTimeMillis(), context.self))
            waitingToStop()
        }.build()
    }

    private fun waitingToStop(): Receive<RacerCommand>?{
        return newReceiveBuilder()
            .onAnyMessage{Behaviors.same()}
            .onSignal(PostStop::class.java){
                println("I am about to terminate")
                Behaviors.same()
            }
            .build()
    }
}