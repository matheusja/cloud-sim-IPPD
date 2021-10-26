
package app;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.vms.Vm;

class MyCrudeCloudletScheduler extends CloudletSchedulerAbstract {
    final private Vm managed;

    public MyCrudeCloudletScheduler(Vm managed) {
        this.managed = managed;
    }

    @Override
    public double cloudletResume(Cloudlet cloudlet) {
        if (managed.getFreePesNumber() >= cloudlet.getNumberOfPes()) {
          return cloudlet.getLength() / managed.getMips();
        }
        return 0;
    }

    @Override
    protected boolean canExecuteCloudletInternal(CloudletExecution cle) {
        return cloudletResume(cle.getCloudlet()) != 0;
    }
}