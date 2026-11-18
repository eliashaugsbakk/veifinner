import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class ALT {
        private class ALTNode {
                final Node node;
                final int gScore;
                final int fScore;

                private ALTNode(Node node, int gScore, int fScore) {
                        this.node = node;
                        this.gScore = gScore;
                        this.fScore = fScore;
                }
        }

        public List<Node> altFraTil(List<Node> noder, Node fraNode, Node tilNode, ALTData altdata) {
                return alt(fraNode, tilNode, altdata);
        }

        private List<Node> alt(Node fra, Node til, ALTData altdata) {

                PriorityQueue<ALTNode> prioritetskø = new PriorityQueue<>(
                    Comparator.comparingInt(a -> a.fScore)
                );

                Map<Node, Node> forrige = new HashMap<>();
                Map<Node, Integer> besteAvstand = new HashMap<>();

                besteAvstand.put(fra, 0);

                int startHeuristikk = beregnHeuristikk(fra, til, altdata);
                prioritetskø.offer(new ALTNode(fra, 0, startHeuristikk));

                while (!prioritetskø.isEmpty()) {

                        ALTNode gjeldende = prioritetskø.poll();

                        // Hopp over utdaterte oppføringer i prioritetskøen
                        if (gjeldende.gScore > besteAvstand.getOrDefault(
                            gjeldende.node, Integer.MAX_VALUE
                        )) {
                                continue;
                        }

                        // Målet er nådd
                        if (gjeldende.node == til) {
                                return reconstructPath(forrige, til);
                        }

                        // Undersøk alle utgående kanter
                        for (Kant kant : gjeldende.node.kanter) {

                                Node nabo = kant.tilNode;

                                int nyAvstand = gjeldende.gScore + kant.kjøretid;

                                if (nyAvstand < besteAvstand.getOrDefault(
                                    nabo, Integer.MAX_VALUE
                                )) {

                                        besteAvstand.put(nabo, nyAvstand);
                                        forrige.put(nabo, gjeldende.node);

                                        int heuristikk = beregnHeuristikk(nabo, til, altdata);
                                        int fScore = nyAvstand + heuristikk;

                                        prioritetskø.offer(
                                            new ALTNode(nabo, nyAvstand, fScore)
                                        );
                                }
                        }
                }

                // Ingen rute funnet
                return new ArrayList<>();
        }

        private List<Node> reconstructPath(Map<Node, Node> forrige, Node nåværende) {
                List<Node> fullstendigVei = new ArrayList<>();

                while (nåværende != null) {
                        fullstendigVei.add(nåværende);
                        nåværende = forrige.get(nåværende);
                }

                Collections.reverse(fullstendigVei);
                return fullstendigVei;
        }

        private int beregnHeuristikk(Node fra, Node til, ALTData altdata) {
                int besteHeuristikk = 0;

                for (int i = 0; i < altdata.hentLandemerker().size(); i++) {

                        int fraLandemerkeTilMål = altdata.hentAvstandFraLandemerke(i, til.nodeNr);

                        int fraLandemerkeTilFra = altdata.hentAvstandFraLandemerke(i, fra.nodeNr);

                        int fraTilLandemerke = altdata.hentAvstandTilLandemerke(i, fra.nodeNr);

                        int målTilLandemerke = altdata.hentAvstandTilLandemerke(i, til.nodeNr);

                        // Første trekantulikhet
                        if (fraLandemerkeTilMål != Integer.MAX_VALUE &&
                            fraLandemerkeTilFra != Integer.MAX_VALUE) {

                                besteHeuristikk = Math.max(besteHeuristikk,
                                    fraLandemerkeTilMål - fraLandemerkeTilFra);
                        }

                        // Andre trekantulikhet
                        if (fraTilLandemerke != Integer.MAX_VALUE &&
                            målTilLandemerke != Integer.MAX_VALUE) {

                                besteHeuristikk =
                                    Math.max(besteHeuristikk, fraTilLandemerke - målTilLandemerke);
                        }
                }

                return besteHeuristikk;
        }
}
