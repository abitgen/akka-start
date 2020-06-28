package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive
import java.io.Serializable
import java.math.BigInteger
import java.util.*
import io.github.abitgen.racingsimulation.WorkerActorBehavior.Companion.Command as WorkerCommand
import io.github.abitgen.racingsimulation.ManagerActorBehavior.Companion.Command as ManagerCommand

class ManagerActorBehavior private constructor(context: ActorContext<ManagerCommand>) : AbstractBehavior<ManagerCommand>(context) {

    private val sortedSet : SortedSet<BigInteger> = TreeSet()
    override fun createReceive(): Receive<ManagerCommand> {
        return newReceiveBuilder()
            .onMessage(InstructionCommand::class.java){
                println("Manager Starts giving task to 20 Workers")
                if(it.msg == "start"){
                    for(i in 0 until 20){
                        val workerActor: ActorRef<WorkerCommand> = context.spawn<WorkerCommand>(WorkerActorBehavior.create(), "worker${i+1}")
                        workerActor.tell(WorkerCommand("start",context.self))
                    }
                }
                this
            }.onMessage(ResultCommand::class.java){
                sortedSet.add(it.prime)
                println("${Thread.currentThread().name} | ${context.self.path()} | Primes received count : ${sortedSet.size}")
                this
            }
            .build()
    }

    companion object {
        fun create(): Behavior<ManagerCommand>? {
            return Behaviors.setup<ManagerCommand> { ManagerActorBehavior(it) }
        }

        interface Command :Serializable
        class InstructionCommand constructor(val msg:String) : Command
        class ResultCommand constructor(val prime:BigInteger) : Command
    }
}