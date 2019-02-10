package com.uic.cloudsim

import org.slf4j.LoggerFactory
import pureconfig.generic.auto._
import JavaObjectMapperFunctions._
import java.util


import org.cloudbus.cloudsim

import scala.collection.JavaConverters
import scala.collection.JavaConverters._
case class Config(simParameter: SimParameter, datacenterList : List[Datacenter],
                  vmList : List[Vm], cloudletList : List[Cloudlet])

/**
  * This class reads the configuration
  * @param configuration
  */
class ConfigReader(configuration : Option[Config]) {

  import ConfigReader.logger

  val config : Config = configuration.get

  def getDatacenterNamesList(): List[String] = {
    config.datacenterList.map(dc => dc.name)

  }

  def getDatacenterList(dcName : String) : util.List[cloudsim.Datacenter] = {
    val dcList : util.List[cloudsim.Datacenter] = config.datacenterList
        .filter(dc => dc.name.equals(dcName))
      .map(mapDatacenter).asJava

      dcList.forEach(dc => {
      logger.debug("Created datacenter: Id {} Name {}",dc.getId, dc.getName)
    })
    return dcList
  }

  def getVmList(brokerId: Int) : util.List[cloudsim.Vm] = {

    val vmList : java.util.List[cloudsim.Vm] =
      config.vmList.map(vm => mapVm(vm,brokerId)).asJava

    vmList.forEach(vm => logger.debug("Created vm: Id {} ",vm.getId))
    return vmList
  }

  def getCloudletList() : util.List[cloudsim.Cloudlet] = {
    val cloudletList : java.util.List[cloudsim.Cloudlet] =
      config.cloudletList.map(mapCloudlet).asJava

    cloudletList.forEach(cl => logger.debug("Created cloudlet : Id {} ",cl.getCloudletId))
    return cloudletList
  }

  def getSimParameter() : SimParameter = {
    logger.debug("Simulation Parameters: No of Users {} Broker Name {} Trace Flag {}",
      config.simParameter.numUsers.toString, config.simParameter.brokerName, config.simParameter.traceFlag.toString)
    return config.simParameter
  }

}

object ConfigReader {
  val logger = LoggerFactory.getLogger(classOf[ConfigReader])
  def apply() : ConfigReader = {

    val configuration = pureconfig.loadConfig[Config]
    configuration.fold(
      l => {
        logger.error("Error {} occurred while reading from config file at {}", l.head.description, l.head.location.get.url.toString:Any)
        new ConfigReader(None)
      },
      r => {
        logger.debug("Picked up config {}", r.toString)
        new ConfigReader(Option(r))
      })
  }

}
