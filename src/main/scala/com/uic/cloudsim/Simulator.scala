package com.uic.cloudsim

import java.util
import java.util.Calendar

import com.uic.cloudsim.SimUtils.printCloudletList
import org.cloudbus.cloudsim
import org.slf4j.LoggerFactory

class Simulator {

  def simulate() = {

    val logger = LoggerFactory.getLogger(classOf[Simulator])

    val reader : ConfigReader = ConfigReader()

    val simParameter : SimParameter = reader.getSimParameter()
    val calendar:Calendar = Calendar.getInstance()
    org.cloudbus.cloudsim.core.CloudSim.init(
      simParameter.numUsers, calendar, simParameter.traceFlag)

    val broker:cloudsim.DatacenterBroker = new cloudsim.DatacenterBroker(simParameter.brokerName)

    val vmList : java.util.List[cloudsim.Vm] = reader.getVmList(broker.getId)


    val cloudletList : java.util.List[cloudsim.Cloudlet] = reader.getCloudletList()

    cloudletList.forEach(cl => cl.setUserId(broker.getId))

    broker.submitVmList(vmList)
    broker.submitCloudletList(cloudletList)

    val dcList : util.List[cloudsim.Datacenter] = reader.getDatacenterList()

    cloudsim.core.CloudSim.startSimulation()
    cloudsim.core.CloudSim.stopSimulation()

    val newList : util.List[cloudsim.Cloudlet] = broker.getCloudletReceivedList()

    printCloudletList(newList)
  }
}
