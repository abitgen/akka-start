package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorSystem

class Main {
}

fun main(){
    val managerActorSystem = ActorSystem.create(ManagerActorBehavior.create(), "managerActorSystem")
    managerActorSystem.tell(ManagerActorBehavior.Companion.InstructionCommand("start"))
}