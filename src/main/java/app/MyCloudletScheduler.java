
package app;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletExecution;
import org.cloudbus.cloudsim.schedulers.cloudlet.CloudletSchedulerAbstract;
import org.cloudbus.cloudsim.vms.Vm;

class MyCloudletScheduler extends CloudletSchedulerAbstract {
    final private Vm managed;

    public MyCloudletScheduler(Vm managed) {
        this.managed = managed;
    }

    @Override
    public double cloudletResume(Cloudlet cloudlet) {
        if ((1 - managed.getCpuPercentUtilization()) >= cloudlet.getUtilizationOfCpu()) {
            return cloudlet.getLength() / managed.getMips();
        }
        return 0;
    }

    @Override
    protected boolean canExecuteCloudletInternal(CloudletExecution cle) {
        return cloudletResume(cle.getCloudlet()) != 0;
    }
}