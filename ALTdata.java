import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
Noder
Nordkapp node:  '2531818 71.1680015 25.7816449' (nord)
Gedser node:    '58898   54.5770445 11.9316767' (sør)
Joensuu node:   '5594864 62.6004072 29.7626200' (øst)
Bergen node:    '5542364 60.3942531  5.3253087' (vest)
 */
public class ALTdata {
        private List<List<Integer>> avstanderFraLandemerker = new ArrayList<>();
        private List<List<Integer>> avstanderTilLandemerker = new ArrayList<>();
        private final List<Integer> landemerker = new ArrayList<>(List.of(
            2531818,
            58898,
            5594864,
            5542364
        ));

        public ALTdata(List<Node> noder, String avstandFil) throws IOException {
                File fil = new File(avstandFil);

                if (!fil.exists()) {
                        preprosessering(noder, fil);
                } else {
                        lesPreprosessering(fil);
                }
        }

        public int hentAvstandFraLandemerke(int landemerkeIndeks, int nodeNr) {
                return avstanderFraLandemerker.get(landemerkeIndeks).get(nodeNr);
        }

        public int hentAvstandTilLandemerke(int landemerkeIndeks, int nodeNr) {
                return avstanderTilLandemerker.get(landemerkeIndeks).get(nodeNr);
        }

        private void preprosessering(List<Node> noder, File avstandFil) throws IOException {
                List<Node> reverserteNoder = reverserGraf(noder);
                Dijkstra dijkstra = new Dijkstra();

                for (int landemerke : landemerker) {
                        // Avstand fra landemerke til alle noder
                        List<Integer> fra = dijkstra.dijkstra(
                            noder, noder.get(landemerke)
                        );
                        avstanderFraLandemerker.add(fra);

                        // Avstand fra alle noder til landemerket
                        // Kjøres på den reverserte grafen
                        List<Integer> til = dijkstra.dijkstra(
                            reverserteNoder, reverserteNoder.get(landemerke)
                        );
                        avstanderTilLandemerker.add(til);
                }

                lagrePreprossesring(avstanderFraLandemerker, avstanderTilLandemerker, avstandFil);
        }

        private void lagrePreprossesring(List<List<Integer>> avstanderFraLandemerker,
            List<List<Integer>> avstanderTilLandemerker, File avstanderFil) throws IOException {
                try (BufferedWriter br = new BufferedWriter(new FileWriter(avstanderFil))) {
                        /*
                        landemerke:2531818      //[landemerkenodeid]
                        0,40033                 //[nodeNr, avstand]
                         */

                        for (int i = 0; i < landemerker.size(); i++) {
                                br.write("landemerke:" + landemerker.get(i) + ":fra\n");

                                for (int j = 0; j < avstanderFraLandemerker.get(i).size(); j++) {
                                        br.write(j + "," + avstanderFraLandemerker.get(i).get(j) + "\n");
                                }

                                br.write("landemerke:" + landemerker.get(i) + ":til\n");

                                for (int j = 0; j < avstanderTilLandemerker.get(i).size(); j++) {
                                        br.write(j + "," + avstanderTilLandemerker.get(i).get(j) + "\n");
                                }
                        }
                }
        }

        private void lesPreprosessering(File avstandFil) throws IOException {
                avstanderFraLandemerker = new ArrayList<>();
                avstanderTilLandemerker = new ArrayList<>();

                for (int i = 0; i < landemerker.size(); i++) {
                        avstanderFraLandemerker.add(new ArrayList<>());
                        avstanderTilLandemerker.add(new ArrayList<>());
                }

                List<Integer> gjeldendeAvstander = null;

                try (BufferedReader br = new BufferedReader(new FileReader(avstandFil))) {
                        String linje;

                        while ((linje = br.readLine()) != null) {
                                if (linje.startsWith("landemerke:")) {
                                        String[] deler = linje.split(":");

                                        int landemerke = Integer.parseInt(deler[1]);
                                        String retning = deler[2];

                                        int indeks = landemerker.indexOf(landemerke);

                                        if (indeks == -1) {
                                                throw new IllegalArgumentException(
                                                    "Ukjent landemerke i ALTdata-filen: " + landemerke
                                                );
                                        }

                                        if (retning.equals("fra")) {
                                                gjeldendeAvstander = avstanderFraLandemerker.get(indeks);
                                        } else if (retning.equals("til")) {
                                                gjeldendeAvstander = avstanderTilLandemerker.get(indeks);
                                        } else {
                                                throw new IllegalArgumentException(
                                                    "Ukjent retning i ALTdata-filen: " + retning
                                                );
                                        }

                                } else if (gjeldendeAvstander != null) {
                                        String[] deler = linje.split(",");

                                        int nodeNr = Integer.parseInt(deler[0]);
                                        int avstand = Integer.parseInt(deler[1]);

                                        if (nodeNr != gjeldendeAvstander.size()) {
                                                throw new IllegalArgumentException(
                                                    "Uventet nodeindeks i ALTdata-filen: " + nodeNr
                                                );
                                        }

                                        gjeldendeAvstander.add(avstand);
                                }
                        }
                }
        }
        private List<Node> reverserGraf(List<Node> noder) {
                List<Node> reverserteNoder = new ArrayList<>(noder.size());

                for (Node n : noder) {
                        reverserteNoder.add(
                            new Node(n.nodeNr, n.breddegrad, n.lengdegrad)
                        );
                }

                for (Node n : noder) {
                        for (Kant kant : n.kanter) {
                                Node fraNode = reverserteNoder.get(kant.tilNode.nodeNr);
                                Node tilNode = reverserteNoder.get(kant.fraNode.nodeNr);

                                Kant reversertKant = new Kant(
                                    fraNode,
                                    tilNode,
                                    kant.kjøretid,
                                    kant.lengde,
                                    kant.fartsgrense
                                );

                                fraNode.kanter.add(reversertKant);
                        }
                }

                return reverserteNoder;
        }
}
