package io.github.abitgen.generateprime

import akka.actor.typed.ActorRef
import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors
import akka.actor.typed.javadsl.Receive

class ManagerActorBehavior(context: ActorContext<String>) : AbstractBehavior<String>(context) {

    override fun createReceive(): Receive<String> {
            return newReceiveBuilder()
                .onMessageEquals("start"){
                    for(i in 0 until 20){
                        val workerActor: ActorRef<String> = context.spawn<String>(WorkerActorBehavior.create(), "worker${i+1}")
                        workerActor.tell("start")
                    }
                    this
                }
                .onAnyMessage {
                println("Any message received : $it")
                this
            }.build()
    }


    companion object{
        fun create(): Behavior<String>? {
            return Behaviors.setup<String> { ManagerActorBehavior(it) }
        }
    }
}