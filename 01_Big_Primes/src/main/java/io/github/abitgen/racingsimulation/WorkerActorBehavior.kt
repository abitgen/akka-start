package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors.setup
import akka.actor.typed.javadsl.Receive
import java.io.Serializable
import java.math.BigInteger
import io.github.abitgen.racingsimulation.WorkerActorBehavior.Companion.Command as WorkerCommand
import io.github.abitgen.racingsimulation.ManagerActorBehavior.Companion.Command as ManagerCommand


class WorkerActorBehavior private constructor(context: ActorContext<WorkerCommand>) : AbstractBehavior<WorkerCommand>(context) {

    override fun createReceive(): Receive<WorkerCommand> {
        return newReceiveBuilder()
                .onMessage(WorkerCommand::class.java) {
                    if (it.msg == "start") {
                        val bigInteger = BigInteger(2000, java.util.Random())
                        println("${Thread.currentThread().name} | ${context.self.path()} | ${bigInteger.nextProbablePrime()}")
                        it.sender.tell(ManagerActorBehavior.Companion.ResultCommand(bigInteger))
                    }
                    this
                }.build()
    }

    companion object{
        fun create(): Behavior<WorkerCommand>? {
            return setup<WorkerCommand> { WorkerActorBehavior(it) }
        }
        class Command constructor(val msg:String, val sender: ActorRef<ManagerCommand>) : Serializable
    }
}