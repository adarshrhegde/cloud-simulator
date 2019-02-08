package com.uic.cloudsim

import java.util.Calendar

import org.slf4j.LoggerFactory
import pureconfig.generic.auto._
import JavaObjectMapperFunctions._
import pureconfig.error.ConfigReaderFailures


case class Config(simParameter: SimParameter, datacenterList : List[Datacenter],
                  vmList : List[Vm], cloudletList : List[Cloudlet])


class ConfigReader {
  val logger = LoggerFactory.getLogger(classOf[ConfigReader])

  def parseConfig : Either[ConfigReaderFailures, Config] = {
    val config = pureconfig.loadConfig[Config]
    System.out.println(config)

    config
  }
}
