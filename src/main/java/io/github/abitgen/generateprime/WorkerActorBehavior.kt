package io.github.abitgen.generateprime

import akka.actor.typed.Behavior
import akka.actor.typed.javadsl.AbstractBehavior
import akka.actor.typed.javadsl.ActorContext
import akka.actor.typed.javadsl.Behaviors.setup
import akka.actor.typed.javadsl.Receive
import java.math.BigInteger

class WorkerActorBehavior(context: ActorContext<String>) : AbstractBehavior<String>(context) {

    override fun createReceive(): Receive<String> {
        return newReceiveBuilder()
            .onMessageEquals("start"){
                val bigInteger = BigInteger(2000, java.util.Random())
                println("${Thread.currentThread().name} ${bigInteger.nextProbablePrime()}")
                this
            }
            .build()
    }

    companion object{
        fun create(): Behavior<String>? {
            return setup<String> { WorkerActorBehavior(it) }
        }
    }
}