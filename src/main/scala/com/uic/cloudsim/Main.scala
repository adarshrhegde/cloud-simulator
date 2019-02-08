package com.uic.cloudsim

import java.text.DecimalFormat
import java.util
import java.util.Calendar

import com.typesafe.config.ConfigFactory
import com.uic.cloudsim.JavaObjectMapperFunctions._
import org.slf4j.LoggerFactory
import pureconfig.error.ConfigReaderFailures
import pureconfig.generic.auto._
import org.cloudbus.cloudsim
import org.cloudbus.cloudsim.Cloudlet

import scala.collection.JavaConverters
case class Settings(id: Int, name: String)
class Main {

    //val config = ConfigFactory.load()

}

object CloudSim extends App {
    val logger = LoggerFactory.getLogger(classOf[Main])
    System.out.println("Hello")

    val reader = new ConfigReader
    val config : Either[ConfigReaderFailures, Config] = reader.parseConfig
    val numUser = 1

    val traceFlag : Boolean = false


    config.fold(
        l => {
            logger.info("Error {} occurred while reading from config file at {}", l.head.description, l.head.location.get.url.toString:Any)
        },
        r => {
            System.out.println(r)
            val calendar:Calendar = Calendar.getInstance()
            org.cloudbus.cloudsim.core.CloudSim.init(
                r.simParameter.numUsers, calendar, r.simParameter.traceFlag)

            val broker:cloudsim.DatacenterBroker = new cloudsim.DatacenterBroker(r.simParameter.brokerName)

            val vmList : java.util.List[cloudsim.Vm] =
                JavaConverters.seqAsJavaList(r.vmList.map(vm => mapVm(vm,broker.getId)))

            val vmIdList : List[Int] = r.vmList.map(vm => vm.vmId)

            val cloudletList : java.util.List[cloudsim.Cloudlet] =
                JavaConverters.seqAsJavaList(r.cloudletList.map(mapCloudlet))

            cloudletList.forEach(cl => cl.setUserId(broker.getId))

            broker.submitVmList(vmList)
            broker.submitCloudletList(cloudletList)

            r.datacenterList.foreach(dc => {
            val datacenter = mapDatacenter(dc)
            System.out.println(datacenter.getName)
            })

            cloudsim.core.CloudSim.startSimulation()
            cloudsim.core.CloudSim.stopSimulation()

            val newList : util.List[cloudsim.Cloudlet] = broker.getCloudletReceivedList()
            val indent = "    "
            val dft : DecimalFormat = new DecimalFormat("###.##")
            logger.info("Cloudlet ID" + indent + "STATUS" + indent
              + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
              + "Start Time" + indent + "Finish Time")
            for(i <- 0 to newList.size()-1){
                val cloudlet : cloudsim.Cloudlet = newList.get(i)
                if(cloudlet.getCloudletStatus == cloudsim.Cloudlet.SUCCESS){
                    logger.info(indent + cloudlet.getCloudletId() + indent + indent +
                      "SUCCESS" + indent + indent + cloudlet.getResourceId()
                      + indent + indent + indent + cloudlet.getVmId()
                      + indent + indent
                      + dft.format(cloudlet.getActualCPUTime()) + indent
                      + indent + dft.format(cloudlet.getExecStartTime())
                      + indent + indent
                      + dft.format(cloudlet.getFinishTime()))
                }

            }

        }
    )

    //val m = new Broker
    //m.simulate
}
