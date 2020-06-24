package io.github.abitgen.generateprime

import akka.actor.typed.ActorSystem

class Main {
}

fun main(){
    val managerActorSystem = ActorSystem.create(ManagerActorBehavior.create(), "managerActorSystem")
    managerActorSystem.tell("start")
}