import java.util.ArrayList;
import java.util.Collections;
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

        public List<Node> dijkstraFraTil(List<Node> noder, Node fraNode, Node tilNode) {
                List<Integer> avstander = new ArrayList<>(noder.size());
                PriorityQueue<DijkstraNode> prioritetskø = new PriorityQueue<>(
                    Comparator.comparingInt(a -> a.avstand));
                // fyll en liste med `null`
                List<Node> forgjenger = new ArrayList<>(
                    Collections.nCopies(noder.size(), null)
                );

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

                        if (dNode.node == tilNode) {
                                return rekonstruerVei(forgjenger, fraNode, tilNode);
                        }

                        // se på kantene til noden
                        for (var k : dNode.node.kanter) {
                                Node nabo = k.tilNode;
                                int nyAvstand = dNode.avstand + k.kjøretid;

                                if (nyAvstand < avstander.get(nabo.nodeNr)) {
                                        avstander.set(nabo.nodeNr, nyAvstand);
                                        forgjenger.set(nabo.nodeNr, dNode.node);
                                        prioritetskø.offer(new DijkstraNode(nabo, nyAvstand));
                                }
                        }
                }

                // fant ingen vei
                return List.of();
        }

        private List<Node> rekonstruerVei(List<Node> forgjenger, Node fraNode, Node tilNode) {
                List<Node> vei = new ArrayList<>();

                Node nåværende = tilNode;

                while (nåværende != null) {
                        vei.add(nåværende);

                        if (nåværende == fraNode) {
                                break;
                        }

                        nåværende = forgjenger.get(nåværende.nodeNr);
                }

                Collections.reverse(vei);
                return vei;
        }

        public List<Node> finnXNærmesteInteressepunkter(List<Node> noder, Node fraNode,
            Interessepunkttype ipt, int antallIpt) {
                if (antallIpt <= 0) {
                        return List.of();
                }

                List<Integer> avstander = new ArrayList<>(noder.size());
                PriorityQueue<DijkstraNode> prioritetskø = new PriorityQueue<>(
                    Comparator.comparingInt(a -> a.avstand));
                List<Node> iptFunnet = new ArrayList<>(antallIpt);

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

                        for (var i : dNode.node.interessepunkter) {
                                if (i.typer.contains(ipt)) {
                                        iptFunnet.add(dNode.node);
                                        break;
                                }
                        }

                        if (iptFunnet.size() >= antallIpt) {
                                return iptFunnet;
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
                // returner de vi fant
                return iptFunnet;
        }
}
