/*
 * CloudSim Plus: A modern, highly-extensible and easier-to-use Framework for
 * Modeling and Simulation of Cloud Computing Infrastructures and Services.
 * http://cloudsimplus.org
 *
 *     Copyright (C) 2015-2018 Universidade da Beira Interior (UBI, Portugal) and
 *     the Instituto Federal de Educação Ciência e Tecnologia do Tocantins (IFTO, Brazil).
 *
 *     This file is part of CloudSim Plus.
 *
 *     CloudSim Plus is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CloudSim Plus is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with CloudSim Plus. If not, see <http://www.gnu.org/licenses/>.
 */
package app;

import org.cloudbus.cloudsim.brokers.DatacenterBroker;
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple;
import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.vms.Vm;
import org.cloudbus.cloudsim.vms.VmSimple;
import org.cloudsimplus.builders.tables.CloudletsTableBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A minimal but organized, structured and re-usable CloudSim Plus example which
 * shows good coding practices for creating simulation scenarios.
 *
 * <p>
 * It defines a set of constants that enables a developer to change the number
 * of Hosts, VMs and Cloudlets to create and the number of {@link Pe}s for
 * Hosts, VMs and Cloudlets.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 * @since CloudSim Plus 1.0
 */
public class App {
    private static final int HOST_PE_MIPS = 1000;
    private static final long HOST_BW = 10_000; // in Megabits/s
    private static final long HOST_STORAGE = 1_000_000; // in Megabytes

    private static final int VMS = 2;
    private static final int VM_PES = 16;

    private final CloudSim simulation;
    private DatacenterBroker broker0;
    private List<Vm> vmList;

    private static final String PATH_NODES = "resources/nodes.tsv";

    public static void main(String[] args) throws IOException {
        SchedulerOption use_refined_scheculer;
        if (args.length >= 2) {
            if (args[1].equals("crude")) {
                use_refined_scheculer = SchedulerOption.MyCrudeScheduler;
            } else if (args[1].equals("refined")) {
                use_refined_scheculer = SchedulerOption.MyScheduler;
            } else if (args[1].equals("default")) {
                use_refined_scheculer = SchedulerOption.SchedulerDefault;
            } else {
                throw new Exception(String.format("Error: \"%s\" not a valid scheduling option", args[1]));
            }
        } else {
            use_refined_scheculer = SchedulerOption.SchedulerDefault;
        }

        new App((args.length >= 1) ? args[0] : "small", use_refined_scheculer);
    }

    private App(String sizeClassName, SchedulerOption so) throws IOException {
        /*
         * Enables just some level of log messages. Make sure to import
         * org.cloudsimplus.util.Log;
         */
        // Log.setLevel(ch.qos.logback.classic.Level.WARN);

        simulation = new CloudSim();
        ArrayList<Datacenter> datacenterList = new Nodes(PATH_NODES).getDatacenters(simulation, HOST_BW, HOST_STORAGE,
                HOST_PE_MIPS);

        // Creates a broker that is a software acting on behalf a cloud customer to
        // manage his/her VMs and Cloudlets
        broker0 = new DatacenterBrokerSimple(simulation);

        for (Datacenter datacenter : datacenterList) {
            List<Cloudlet> cloudletList = JobScanner
                    .scanJobsFile(String.format("resources/jobs/%s_%s.tsv", datacenter.getName(), sizeClassName));
            broker0.submitCloudletList(cloudletList);
        }

        vmList = createVms(datacenterList, so);
        broker0.submitVmList(vmList);

        simulation.start();

        final List<Cloudlet> finishedCloudlets = broker0.getCloudletFinishedList();
        new CloudletsTableBuilder(finishedCloudlets).build();
    }

    /**
     * Creates a list of VMs.
     */
    private List<Vm> createVms(List<Datacenter> datacenterList, SchedulerOption so) {
        final List<Vm> list = new ArrayList<>(VMS);
        for (Datacenter datacenter : datacenterList) {
            for (Host host : datacenter.getHostList()) {
                for (int i = 0; i < host.getPeList().size() / VM_PES; i++) {
                    // Uses a CloudletSchedulerTimeShared by default to schedule Cloudlets
                    final Vm vm = new VmSimple(HOST_PE_MIPS, VM_PES);
                    vm.setRam(512).setBw(1000).setSize(10_000);
                    switch (so) {
                        case MyCrudeScheduler:
                            vm.setCloudletScheduler(new MyCrudeCloudletScheduler(vm));
                            break;
                        case MyScheduler:
                            vm.setCloudletScheduler(new MyCloudletScheduler(vm));
                            break;
                        default:
                            break;
                    }
                    list.add(vm);
                }
            }
        }
        return list;
    }

    /**
     * Creates a list of Cloudlets.
     */
}

enum SchedulerOption {
    SchedulerDefault, MyCrudeScheduler, MyScheduler
}