package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.cloudbus.cloudsim.cloudlets.Cloudlet;
import org.cloudbus.cloudsim.cloudlets.CloudletSimple;

import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic;

public class JobScanner {
    public static List<Cloudlet> scanJobsFile(String path) throws FileNotFoundException {
        Scanner scanner = new Scanner(new FileInputStream(path));
        List<Cloudlet> jobList = new ArrayList<>();
        
        try {
            scanner.nextLine(); // Ignore first line(header)
            double atrasoAcumulado = 0;
            while (scanner.hasNext()) {
                String[] elems = scanner.nextLine().split("\t");
                
                atrasoAcumulado += Double.parseDouble(elems[0]);
                int numeroTarefas = Integer.parseInt(elems[1]);
                int custoComputacional = Integer.parseInt(elems[2]);

                for (int i = 0; i < numeroTarefas; i++) {
                    Cloudlet cloudlet = new CloudletSimple(custoComputacional, 1);
                    cloudlet.setSubmissionDelay(atrasoAcumulado);
                    cloudlet.setUtilizationModelCpu(new UtilizationModelDynamic(0.1));
                    jobList.add(cloudlet);
                }
            }
        } finally {
            scanner.close();
        }

        return jobList;
    }
}
