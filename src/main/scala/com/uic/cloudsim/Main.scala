package com.uic.cloudsim


import com.uic.cloudsim.JavaObjectMapperFunctions._
import pureconfig.generic.auto._
import SimUtils._

case class Settings(id: Int, name: String)
class Main {



}

object CloudSim extends App {

  val sim = new Simulator
  sim.simulate()

}
