package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import org.cloudbus.cloudsim.core.Simulation;

import org.cloudbus.cloudsim.datacenters.Datacenter;
import org.cloudbus.cloudsim.datacenters.DatacenterSimple;
import org.cloudbus.cloudsim.hosts.Host;
import org.cloudbus.cloudsim.hosts.HostSimple;
import org.cloudbus.cloudsim.resources.Pe;
import org.cloudbus.cloudsim.resources.PeSimple;

public class Nodes {
  public final ArrayList<String> nomes;
  public final ArrayList<Integer> niveis;
  public final ArrayList<Integer> processingElements;
  public final ArrayList<Integer> processingMods;
  public final ArrayList<Integer> RAMs;
  
  public Nodes(String path) throws IOException {
    nomes = new ArrayList<>();
    niveis = new ArrayList<>();
    processingMods = new ArrayList<>();
    processingElements = new ArrayList<>();
    RAMs = new ArrayList<>();
    
    Scanner s = new Scanner(new FileInputStream(path));
    try {
      s.nextLine(); // Ignore first line(header)
      while (s.hasNext()) {
        String[] elems = s.nextLine().split("\t");
        nomes.add(elems[0]);
        niveis.add(Integer.parseInt(elems[1]));
        processingElements.add(Integer.parseInt(elems[3]));
        processingMods.add(Integer.parseInt(elems[2]));
        RAMs.add(Integer.parseInt(elems[4]) * 1024);
      }
    } finally {
      s.close();
    }
  }
  public ArrayList<Datacenter> getDatacenters(Simulation s, long hostBw, long hostStorage, long pe_mips) {
    ArrayList<Datacenter> result = new ArrayList<>();
    for (int i = 0; i < nomes.size(); i++) {
      ArrayList<Host> hosts = new ArrayList<>();
      for (int j = 0; j < processingMods.get(i); j++) {
        ArrayList<Pe> pes = new ArrayList<>();
        for (int k = 0; k < processingElements.get(i) / processingMods.get(i); k++) {
            pes.add(new PeSimple(pe_mips));
        }
        hosts.add(new HostSimple(RAMs.get(i), hostBw, hostStorage, pes));
      }
      Datacenter datacenter = new DatacenterSimple(s, hosts);
      datacenter.setName(nomes.get(i));
      result.add(datacenter);
    }
    return result;
  }
}
