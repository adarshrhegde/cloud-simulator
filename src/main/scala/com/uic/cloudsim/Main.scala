package com.uic.cloudsim

import java.util
import java.util.Calendar
import com.uic.cloudsim.JavaObjectMapperFunctions._
import org.slf4j.LoggerFactory
import pureconfig.generic.auto._
import org.cloudbus.cloudsim
import SimUtils._

case class Settings(id: Int, name: String)
class Main {

    //val config = ConfigFactory.load()

}

object CloudSim extends App {

  val sim = new Simulator
  sim.simulate()

}
