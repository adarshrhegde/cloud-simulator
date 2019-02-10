package com.uic.cloudsim

import java.util
import java.util.Calendar

import com.uic.cloudsim.SimUtils.printCloudletList
import org.cloudbus.cloudsim
import org.slf4j.LoggerFactory

import scala.collection.mutable
import scala.collection.mutable.HashMap
class Simulator {

  def simulate() = {

    val logger = LoggerFactory.getLogger(classOf[Simulator])

    logger.info("Begin simulation!")
    val reader : ConfigReader = ConfigReader()

    val simParameter : SimParameter = reader.getSimParameter()
    val calendar:Calendar = Calendar.getInstance()

    val cloudletCostMap : HashMap[Int, Double] = new HashMap[Int, Double]()
    val cloudletDcMap : HashMap[Int, String] = new mutable.HashMap[Int, String]()
    val dcNamesList : List[String] = reader.getDatacenterNamesList()

    dcNamesList.foreach(dc => {

      logger.info("Performing simulation for Datacenter {}", dc)
      cloudsim.core.CloudSim.init(
        simParameter.numUsers, calendar, simParameter.traceFlag)

      val broker:cloudsim.DatacenterBroker = new cloudsim.DatacenterBroker(simParameter.brokerName)
      logger.debug("Created broker {}", simParameter.brokerName)

      logger.debug("Creating VMs")
      val vmList : java.util.List[cloudsim.Vm] = reader.getVmList(broker.getId)


      logger.debug("Creating Cloudlets")
      val cloudletList : java.util.List[cloudsim.Cloudlet] = reader.getCloudletList()

      cloudletList.forEach(cl => cl.setUserId(broker.getId))

      logger.debug("Submitting VM list and cloudlet list to broker")
      broker.submitVmList(vmList)
      broker.submitCloudletList(cloudletList)

      val dcList : util.List[cloudsim.Datacenter] = reader.getDatacenterList(dc)

      cloudsim.core.CloudSim.startSimulation()
      cloudsim.core.CloudSim.stopSimulation()

      val newList : util.List[cloudsim.Cloudlet] = broker.getCloudletReceivedList()

      logger.debug("Calculating costs")
      newList.forEach(cl => {
        if(!cloudletCostMap.contains(cl.getCloudletId) || cl.getProcessingCost < cloudletCostMap.get(cl.getCloudletId).get)
          {
            cloudletCostMap += cl.getCloudletId -> cl.getProcessingCost
            cloudletDcMap += cl.getCloudletId -> dc
          }

         })
      printCloudletList(newList)

    })

    cloudletDcMap.foreach((item) => {
      logger.info("Cloudlet {} can be run optimally in Datacenter {}", item._1.toString(), item._2.toString:Any)

    })

    logger.info("End simulation!")
  }
}
