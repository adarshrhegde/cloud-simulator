package com.uic.cloudsim

class DomainObjects {}

case class Datacenter(name :String, datacenterCharacteristics: DatacenterCharacteristics,
                      vmAllocationPolicy: String, schedulingInterval : Double)

case class DatacenterCharacteristics(arch : String, os : String, vmm: String, hostList : List[Host],
                                     timeZone : Double, cost : Double, costPerMem : Double,
                                     costPerBw : Double, costPerStorage : Double)

case class Pe(peId: Int, peProvisioner: String, mips: Long)

case class VmAllocationPolicy(vmAllocationPolicy: String, hostList : List[Host])

case class Host(hostId : Int, ramProvisioner: String, ram: Int, bwProvisioner: String,
                bw : Int, storage:Long, peList: List[Pe], vmScheduler : String)

case class RamProvisioner(ramProvisioner: String, ram: Int)

case class BwProvisioner(bwProvisioner: String, bw : Int)

case class VmScheduler(vmScheduler: String, peList : List[Pe])


case class Vm(vmId: Int, mips : Int, size : Long, ram : Int,
              bw : Long, pesNumber : Int, vmm : String,
              cloudletScheduler: String)

case class Cloudlet(id: Int, length : Long, pesNumber : Int, fileSize : Long,
                    outputSize : Long, utilizationModelCpu : String,
                    utilizationModelRam : String, utilizationModelBw : String
                   )

case class SimParameter(numUsers: Int, traceFlag: Boolean, brokerName: String)



