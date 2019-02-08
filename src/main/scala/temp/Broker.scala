package temp

import java.text.DecimalFormat
import java.util
import java.util.Calendar

import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}
import org.cloudbus.cloudsim.{Cloudlet, CloudletSchedulerTimeShared, Datacenter, DatacenterBroker, DatacenterCharacteristics, Host, Pe, Storage, UtilizationModel, UtilizationModelFull, Vm, VmAllocationPolicySimple, VmSchedulerTimeShared}
import org.slf4j.LoggerFactory

class Broker {

  val logger = LoggerFactory.getLogger(classOf[Broker])
    def simulate: Unit = {

      logger.info("Starting sim")
      var cloudletList : util.List[Cloudlet] = new util.ArrayList[Cloudlet]()
      /** The vmlist. */
      var vmlist : util.List[Vm] = new util.ArrayList[Vm]()
      val name = "Datacenter0"
      val numUsers = 1
      val calendar : Calendar = Calendar.getInstance()
      val traceFlag : Boolean = false
      CloudSim.init(numUsers, calendar, traceFlag)

      val hostList :util.List[Host] = new util.ArrayList[Host]();

      // 2. A Machine contains one or more PEs or CPUs/Cores.
      // In this example, it will have only one core.

      val peList : util.List[Pe] = new util.ArrayList[Pe]();

      val mips: Int = 1000

      // 3. Create PEs and add these into a list.
      peList.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating

      // 4. Create Host with its id and list of PEs and add them to the list
      // of machines
      val hostId :Int = 0;
      val ram : Int = 2048; // host memory (MB)
      val storage : Long = 1000000; // host storage
      val bw : Int = 10000;

      hostList.add(
        new Host(
          hostId,
          new RamProvisionerSimple(ram),
          new BwProvisionerSimple(bw),
          storage,
          peList,
          new VmSchedulerTimeShared(peList)
        )
      ); // This is our machine

      // 5. Create a DatacenterCharacteristics object that stores the
      // properties of a data center: architecture, OS, list of
      // Machines, allocation policy: time- or space-shared, time zone
      // and its price (G$/Pe time unit).
      val arch : String = "x86"; // system architecture
      val os: String  = "Linux"; // operating system
      val vmm : String  = "Xen";
      val time_zone : Double = 10.0; // time zone this resource located
      val cost : Double = 3.0; // the cost of using processing in this resource
      val costPerMem : Double = 0.05; // the cost of using memory in this resource
      val costPerStorage: Double = 0.001; // the cost of using storage in this
      // resource
      val costPerBw : Double = 0.0; // the cost of using bw in this resource
      val storageList : util.LinkedList[Storage] = new util.LinkedList[Storage](); // we are not adding SAN
      // devices by now

      val characteristics : DatacenterCharacteristics  = new DatacenterCharacteristics(
        arch, os, vmm, hostList, time_zone, cost, costPerMem,
        costPerStorage, costPerBw)

      // 6. Finally, we need to create a PowerDatacenter object.
      var datacenter : Datacenter = null
      try {
        datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
      } catch  {
        case e: Exception => e.printStackTrace()
      }

      val broker: DatacenterBroker = new DatacenterBroker("Broker")
      val brokerId : Int = broker.getId()

      val vmid : Int = 0
      val vmMips : Int = 1000
      val vmSize : Int = 10000
      val vmRam : Int = 512
      val vmBw : Int = 1000
      val vmPesNumber : Int = 1
      val VimVmm : String = "Xen"

      val vm : Vm = new Vm(vmid, brokerId, mips, vmPesNumber, vmRam, vmBw,
      vmSize, VimVmm, new CloudletSchedulerTimeShared())

      vmlist.add(vm)

      broker.submitVmList(vmlist)

      val id : Int = 0
      val length : Long = 400000
      val fileSize : Long = 300
      val outputSize : Long = 300
      val utilizationModel : UtilizationModel = new UtilizationModelFull()

      val cloudlet : Cloudlet =
        new Cloudlet(id, length, vmPesNumber, fileSize,
          outputSize, utilizationModel, utilizationModel,
          utilizationModel)
      cloudlet.setUserId(brokerId)
      cloudlet.setVmId(vmid)

      // add the cloudlet to the list
      cloudletList.add(cloudlet)

      // submit cloudlet list to the broker
      broker.submitCloudletList(cloudletList)

      CloudSim.startSimulation()
      CloudSim.stopSimulation()

      val newList : util.List[Cloudlet] = broker.getCloudletReceivedList()
      val indent = "    "
      val dft : DecimalFormat = new DecimalFormat("###.##")
      logger.info("Cloudlet ID" + indent + "STATUS" + indent
        + "Data center ID" + indent + "VM ID" + indent + "Time" + indent
        + "Start Time" + indent + "Finish Time")
      for(i <- 0 to newList.size()-1){
        val cloudlet : Cloudlet = newList.get(i)
        if(cloudlet.getCloudletStatus == Cloudlet.SUCCESS){
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

      logger.info("Sim finished")
    }

}

object Broker extends App {

  System.out.println("Hello")
  //val b = new Broker
  //b.simulate
}
