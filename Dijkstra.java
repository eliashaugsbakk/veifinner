import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;


public class Dijkstra {
        private class DijkstraNode {
                final Node node;
                final int avstand;

                DijkstraNode(Node node, int avstand) {
                        this.node = node;
                        this.avstand = avstand;
                }
        }

        public List<Integer> dijkstra(List<Node> noder, Node fraNode) {
                List<Integer> avstander = new ArrayList<>(noder.size());
                PriorityQueue<DijkstraNode> prioritetskø = new PriorityQueue<>(
                    Comparator.comparingInt(a -> a.avstand));

                for (int i = 0; i < noder.size(); i++) {
                        avstander.add(Integer.MAX_VALUE);
                }

                avstander.set(fraNode.nodeNr, 0);
                prioritetskø.offer(new DijkstraNode(fraNode, 0));

                while (!prioritetskø.isEmpty()) {
                        // hent ut og fjerner noden med lavest avstand
                        var dNode = prioritetskø.poll();

                        // sjekk om avstanden er den beste vi kjenner
                        if (dNode.avstand > avstander.get(dNode.node.nodeNr)) {
                                continue;
                        }

                        // se på kantene til noden
                        for (var k : dNode.node.kanter) {
                                Node nabo = k.tilNode;
                                int nyAvstand = dNode.avstand + k.kjøretid;

                                if (nyAvstand < avstander.get(nabo.nodeNr)) {
                                        avstander.set(nabo.nodeNr, nyAvstand);
                                        prioritetskø.offer(new DijkstraNode(nabo, nyAvstand));
                                }
                        }
                }
                return avstander;
        }
}
