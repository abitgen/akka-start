package io.github.abitgen.racingsimulation

import akka.actor.typed.ActorSystem
import io.github.abitgen.racingsimulation.Consts.DataConstants.TOTAL_RACERS
import io.github.abitgen.racingsimulation.helper.MarathonHelper

fun main(){
    val managerActorSystem = ActorSystem.create(MarathonController.create { MarathonHelper(TOTAL_RACERS) }, "marathonActorSystem")
    managerActorSystem.tell(MarathonController.Companion.StartCommand("start"))
}