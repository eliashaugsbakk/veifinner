import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NodeKantInnleser {
        public static List<Node> lesNoder(String nodefilsti) {
                List<Node> nodeliste = null;
                try (BufferedReader br = new BufferedReader(new FileReader(nodefilsti))) {
                        int antallLinjer = Integer.parseInt(br.readLine().strip());
                        nodeliste = new ArrayList<>(antallLinjer);
                        String linje;
                        while ((linje = br.readLine()) != null) {
                                nodeliste.add(lesNode(linje));
                        }
                } catch (IOException e) {
                        System.err.println("Klarte ikke lese inn noder: " + e.getMessage());
                }
                return nodeliste;
        }

        private static Node lesNode(String linje) {
                String[] nodeData = linje.split("\\s+");
                int nodeNr = Integer.parseInt(nodeData[0].strip());
                double breddegrad = Double.parseDouble(nodeData[1].strip());
                double lengdegrad = Double.parseDouble(nodeData[2].strip());
                return new Node(nodeNr, breddegrad, lengdegrad);
        }


        public static List<Kant> lesKanter(String kantfilsti, List<Node> noder) {
                List<Kant> kantliste = null;
                try (BufferedReader br = new BufferedReader(new FileReader(kantfilsti))) {
                        int antallLinjer = Integer.parseInt(br.readLine().strip());
                        kantliste = new ArrayList<>(antallLinjer);
                        String linje;
                        while ((linje = br.readLine()) != null) {
                                kantliste.add(lesKant(linje, noder));
                        }
                } catch (IOException e) {
                        System.err.println("Klarte ikke lese inn kanter: " + e.getMessage());
                }
                return kantliste;
        }

        private static Kant lesKant(String line, List<Node> noder) {
                String[] kantData = line.split("\\s+");
                Node fraNode = noder.get(Integer.parseInt(kantData[0]));
                Node tilNode = noder.get(Integer.parseInt(kantData[1]));
                return new Kant(
                        fraNode,
                        tilNode,
                        Integer.parseInt(kantData[2].strip()),
                        Integer.parseInt(kantData[3].strip()),
                        Integer.parseInt(kantData[4].strip())
                );
        }
}
