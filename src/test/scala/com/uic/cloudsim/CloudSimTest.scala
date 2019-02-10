package com.uic.cloudsim
import org.scalatest.FunSuite
import java.util
import java.util.Calendar

import JavaObjectMapperFunctions._
import org.cloudbus.cloudsim
import org.cloudbus.cloudsim.{DatacenterBroker, DatacenterCharacteristics, Host, Pe, Storage, VmSchedulerTimeShared}
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import pureconfig.error.ConfigReaderFailures
class CloudSimTest extends FunSuite {

  test("Configuration test"){
    // Check if  configuration is loaded correctly (no errors)
    val reader : ConfigReader = ConfigReader()
    assert(reader.config.getClass != ConfigReaderFailures.getClass)

  }

  test("Simulation element creation test"){

    // Test creation of simulation elements
    val reader : ConfigReader = ConfigReader()

    val sim : SimParameter = reader.getSimParameter()
    cloudsim.core.CloudSim.init(sim.numUsers, Calendar.getInstance(), sim.traceFlag)
    val cloudletList : util.List[cloudsim.Cloudlet] = reader.getCloudletList()

    // ensure all cloudlets are loaded
    assert(cloudletList.size() == 2)


    // Ensure only one DC with given name
    assert(reader.getDatacenterList("DC1").size() == 1)

    cloudsim.core.CloudSim.finishSimulation()
  }

  test("Datacenter setup test"){
    val reader : ConfigReader = ConfigReader()

    val sim : SimParameter = reader.getSimParameter()
    cloudsim.core.CloudSim.init(sim.numUsers, Calendar.getInstance(), sim.traceFlag)




    val peList : List[Pe] = List[Pe](new Pe(0, "PeProvisionerSimple",121));

    val mips: Int = 1000


    val hostId :Int = 0;
    val ram : Int = 2048; // host memory (MB)
    val storage : Long = 1000000; // host storage
    val bw : Int = 10000;

    val hostList :List[Host] = List[Host](
      new Host(
        hostId,
        "RamProvisionerSimple",
        ram,"BwProvisionerSimple",
        bw,
        storage,peList,
        "VmSchedulerTimeSharedSimple"
      ))


    val arch : String = "x86";
    val os: String  = "Linux";
    val vmm : String  = "Xen";
    val time_zone : Double = 10.0;
    val cost : Double = 3.0;
    val costPerMem : Double = 0.05;
    val costPerStorage: Double = 0.001;
    // resource
    val costPerBw : Double = 0.0;
    val storageList : util.LinkedList[Storage] = new util.LinkedList[Storage](); // we are not adding SAN
    // devices by now

    val characteristics : DatacenterCharacteristics  = new DatacenterCharacteristics(
      arch, os, vmm, hostList, time_zone, cost, costPerMem,
      costPerStorage, costPerBw)



    val dc : Datacenter = new Datacenter("DC3", characteristics,
      "VmAllocationPolicySimple", 0)

    mapDatacenter(dc)
    assert(cloudsim.core.CloudSim.getEntityList().size() == 3)
    cloudsim.core.CloudSim.finishSimulation()

  }

  test("VMAllocation Policy test"){

    val reader : ConfigReader = ConfigReader()

    val sim : SimParameter = reader.getSimParameter()
    cloudsim.core.CloudSim.init(sim.numUsers, Calendar.getInstance(), sim.traceFlag)


    val peList : List[Pe] = List[Pe](new Pe(0, "PeProvisionerSimple",121));

    val mips: Int = 1000


    val hostId :Int = 0;
    val ram : Int = 2048; // host memory (MB)
    val storage : Long = 1000000; // host storage
    val bw : Int = 10000;
    val hostList :List[Host] = List[Host](
      new Host(
        hostId,
        "RamProvisionerSimple",
        ram,"BwProvisionerSimple",
        bw,
        storage,peList,
        "VmSchedulerTimeSharedSimple"
      ))
    val vmAlloc: cloudsim.VmAllocationPolicy = mapVmAllocationPolicy(VmAllocationPolicy("VmAllocationPolicySimple", hostList))
    // Test if allocation policy is assigned to right host
    assert(vmAlloc.getHostList[cloudsim.Host]().get(0).getId == hostId)
    cloudsim.core.CloudSim.finishSimulation()
  }

  test("Broker test"){

    val reader : ConfigReader = ConfigReader()

    val sim : SimParameter = reader.getSimParameter()
    cloudsim.core.CloudSim.init(sim.numUsers, Calendar.getInstance(), sim.traceFlag)

    val broker: DatacenterBroker = new DatacenterBroker(sim.brokerName)
    assert(broker.getId != 0)
    cloudsim.core.CloudSim.finishSimulation()
  }


}
