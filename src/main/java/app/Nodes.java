package app;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.cloudbus.cloudsim.datacenters.Datacenter;
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
        processingElements.add(Integer.parseInt(elems[2]));
        processingMods.add(Integer.parseInt(elems[3]));
        RAMs.add(Integer.parseInt(elems[4]) * 1024);
      }
    } finally {
      s.close();
    }
  }
  public ArrayList<Host> getHosts(long hostBw, long hostStorage) {
    ArrayList<Host> result = new ArrayList<>();
    for (int i = 0; i < nomes.size(); i++) {
      ArrayList<Pe> pes = new ArrayList<Pe>();
      for (int j = 0; j < processingMods.get(i); j++){
        pes.add(new PeSimple(processingElements.get(i) * 10_000));
      }
      result.add(new HostSimple(RAMs.get(i), hostBw, hostStorage, pes));
    }
    return result;
  }
}
