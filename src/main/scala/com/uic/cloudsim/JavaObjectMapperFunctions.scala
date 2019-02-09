package com.uic.cloudsim


import java.util

import com.uic.cloudsim.ConfigReader.logger
import org.cloudbus.cloudsim
import org.cloudbus.cloudsim.{CloudletScheduler, Storage, UtilizationModel, UtilizationModelFull, UtilizationModelNull, UtilizationModelPlanetLabInMemory, UtilizationModelStochastic, provisioners}
import org.cloudbus.cloudsim.provisioners.{BwProvisionerSimple, PeProvisionerSimple, RamProvisionerSimple}

import scala.collection.JavaConverters

object JavaObjectMapperFunctions {

  implicit var mapPe = new Function[Pe, org.cloudbus.cloudsim.Pe] {

    def apply(pe: Pe): cloudsim.Pe = pe.peProvisioner match {
      // Case for different types of PE Provisioners
      case "PeProvisionerSimple" => new org.cloudbus.cloudsim.Pe(pe.peId,
        new PeProvisionerSimple(pe.mips))
      // Default case for Pe Provisioner simple
      case _ => new org.cloudbus.cloudsim.Pe(pe.peId, new PeProvisionerSimple(pe.mips))

    }
  }


  implicit var mapRamProvisioner =
    new Function[RamProvisioner, provisioners.RamProvisioner] {

    override def apply(ramProvisioner : RamProvisioner): provisioners.RamProvisioner =

      ramProvisioner.ramProvisioner match {
      case "RamProvisionerSimple" => new RamProvisionerSimple(ramProvisioner.ram)

      // default case for incorrect config
      case _ => new RamProvisionerSimple(ramProvisioner.ram)
    }
  }

  implicit var mapBwProvisioner =
    new Function[BwProvisioner, provisioners.BwProvisioner] {
      override def apply(bwProvisioner: BwProvisioner) : provisioners.BwProvisioner =

        bwProvisioner.bwProvisioner match {
          case "BwProvisionerSimple" => new BwProvisionerSimple(bwProvisioner.bw)

          case _ => new BwProvisionerSimple(bwProvisioner.bw)
        }

    }

  implicit var mapVmScheduler = new Function[VmScheduler, cloudsim.VmScheduler] {
    override def apply(vmScheduler: VmScheduler): cloudsim.VmScheduler =
      vmScheduler.vmScheduler match {
        case "VmSchedulerTimeShared" => new cloudsim.VmSchedulerTimeShared(
          JavaConverters.seqAsJavaList[cloudsim.Pe](vmScheduler.peList.map(mapPe)))

        case "VmSchedulerSpaceShared" => new cloudsim.VmSchedulerSpaceShared(
          JavaConverters.seqAsJavaList[cloudsim.Pe](vmScheduler.peList.map(mapPe)))

        case "VmSchedulerTimeSharedOverSubscription" => new cloudsim.VmSchedulerTimeSharedOverSubscription(
          JavaConverters.seqAsJavaList[cloudsim.Pe](vmScheduler.peList.map(mapPe)))

        case _ => new cloudsim.VmSchedulerTimeShared(
          JavaConverters.seqAsJavaList[cloudsim.Pe](vmScheduler.peList.map(mapPe)))
      }
  }

  implicit var mapHost = new Function[Host, org.cloudbus.cloudsim.Host] {
    override def apply(host: Host): cloudsim.Host = {

      new org.cloudbus.cloudsim.Host(host.hostId,
        mapRamProvisioner(RamProvisioner(host.ramProvisioner, host.ram)),
        mapBwProvisioner(BwProvisioner(host.bwProvisioner, host.bw)),
        host.storage,
        JavaConverters.seqAsJavaList[cloudsim.Pe](host.peList.map(mapPe)),
        mapVmScheduler(VmScheduler(host.vmScheduler, host.peList)))
    }
  }

  implicit  var mapDatacenterCharacteristics =
    new Function[DatacenterCharacteristics, cloudsim.DatacenterCharacteristics] {

      override def apply(dcChars: DatacenterCharacteristics): cloudsim.DatacenterCharacteristics = {
        new cloudsim.DatacenterCharacteristics(
          dcChars.arch,
          dcChars.os,
          dcChars.vmm,
          JavaConverters.seqAsJavaList[cloudsim.Host](dcChars.hostList.map(mapHost)),
          dcChars.timeZone,
          dcChars.cost,
          dcChars.costPerMem,
          dcChars.costPerStorage,
          dcChars.costPerBw
        )

      }
    }

  implicit var mapVmAllocationPolicy =
    new Function[VmAllocationPolicy, cloudsim.VmAllocationPolicy] {
      override def apply(vmAllocationPolicy: VmAllocationPolicy): cloudsim.VmAllocationPolicy = {
        vmAllocationPolicy.vmAllocationPolicy match {

          case "VmAllocationPolicySimple" =>
            new cloudsim.VmAllocationPolicySimple(
              JavaConverters.seqAsJavaList[cloudsim.Host](vmAllocationPolicy.hostList.map(mapHost))
            )

          case _ =>
            new cloudsim.VmAllocationPolicySimple(
              JavaConverters.seqAsJavaList[cloudsim.Host](vmAllocationPolicy.hostList.map(mapHost))
            )
        }
      }
    }

  implicit  var mapDatacenter =
    new Function[Datacenter, cloudsim.Datacenter] {
    override def apply(dc: Datacenter): cloudsim.Datacenter = {

      new cloudsim.Datacenter(dc.name,
        mapDatacenterCharacteristics(dc.datacenterCharacteristics),
        mapVmAllocationPolicy(VmAllocationPolicy(dc.vmAllocationPolicy,dc.datacenterCharacteristics.hostList)),
        new util.LinkedList[Storage](), dc.schedulingInterval)

    }
  }

  implicit var mapCloudletScheduler =
    new Function3[String, Int, Int, cloudsim.CloudletScheduler] {
      override def apply(cs: String, mips:Int, pesNumber : Int): CloudletScheduler = {
        cs match {
          case "CloudletSchedulerTimeShared" => new cloudsim.CloudletSchedulerTimeShared()
          case "CloudletSchedulerSpaceShared" => new cloudsim.CloudletSchedulerSpaceShared()
          case "CloudletSchedulerDynamicWorkload" => new cloudsim.CloudletSchedulerDynamicWorkload(mips, pesNumber)

        }
      }
    }

  implicit var mapVm =
    new Function2[Vm, Int, cloudsim.Vm] {
    override def apply(vm: Vm, brokerId: Int): cloudsim.Vm = {
      new cloudsim.Vm(vm.vmId, brokerId, vm.mips,
        vm.pesNumber, vm.ram, vm.bw, vm.size, vm.vmm,
        mapCloudletScheduler(vm.cloudletScheduler, vm.mips, vm.pesNumber))

    }
  }

  implicit  var mapUtilizationModel =
    new Function[String, cloudsim.UtilizationModel] {
      override def apply(model: String): UtilizationModel = {
        model match {
          case "UtilizationModelFull" => new UtilizationModelFull()
          case "UtilizationModelNull" => new UtilizationModelNull()
          case "UtilizationModelStochastic" => new UtilizationModelStochastic()
          case _ => new UtilizationModelFull()

        }
      }
    }

  implicit var mapCloudlet =
    new Function[Cloudlet, cloudsim.Cloudlet] {
      override def apply(cloudlet: Cloudlet): cloudsim.Cloudlet = {
        new cloudsim.Cloudlet(cloudlet.id, cloudlet.length, cloudlet.pesNumber, cloudlet.fileSize,
          cloudlet.outputSize, mapUtilizationModel(cloudlet.utilizationModelCpu),
          mapUtilizationModel(cloudlet.utilizationModelRam),
          mapUtilizationModel(cloudlet.utilizationModelBw))
      }
    }

}
