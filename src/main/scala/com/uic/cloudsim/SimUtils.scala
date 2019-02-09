package com.uic.cloudsim

import java.text.DecimalFormat

import org.cloudbus.cloudsim
import org.slf4j.LoggerFactory

class SimUtils {}

object SimUtils {

  val logger = LoggerFactory.getLogger(classOf[SimUtils])
  implicit def printCloudletList(cloudletList: java.util.List[cloudsim.Cloudlet]) = {

    val indent = "    "
    val dft: DecimalFormat = new DecimalFormat("###.##")

    logger.info("Cloudlet ID" + indent + "STATUS" + indent
      + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
      + "Start Time" + indent + "Finish Time")

    for (i <- 0 to cloudletList.size() - 1) {
      val cloudlet: cloudsim.Cloudlet = cloudletList.get(i)
      if (cloudlet.getCloudletStatus == cloudsim.Cloudlet.SUCCESS) {
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
}
