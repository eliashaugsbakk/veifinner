/**
 * Denne filen er fullstendig generert av Microsoft Copilot.
 */
import java.util.EnumSet;
import java.util.Set;

private enum Algoritme {
        DIJKSTRA,
        ALT
}


private static class Sesjon {
        final List<Node> noder;
        final String altFil;
        ALTData altdata;

        Sesjon(List<Node> noder, String altFil) {
                this.noder = noder;
                this.altFil = altFil;
        }

        ALTData hentALTdata() throws IOException {
                if (altdata == null) {
                        long start = System.nanoTime();
                        IO.println("Laster eller beregner ALT-data...");
                        altdata = new ALTData(noder, altFil);
                        skrivTid("Klargjøring av ALT-data", System.nanoTime() - start);
                }
                return altdata;
        }
}

void main(String[] args) {
        try {
                start(args);
        } catch (IOException e) {
                System.err.println("Filfeil: " + e.getMessage());
                System.exit(1);
        }
}

private static void start(String[] args) throws IOException {
        if (args.length != 0 && args.length != 3 && args.length != 4) {
                throw new IllegalArgumentException(
                    "Bruk: Main [noder.txt kanter.txt interessepkt.txt [altdata.txt]]"
                );
        }

        String nodeFil = args.length == 0 ? "norden/noder.txt" : args[0];
        String kantFil = args.length == 0 ? "norden/kanter.txt" : args[1];
        String interessepunktFil = args.length == 0
            ? "norden/interessepkt.txt" : args[2];
        String altFil = args.length == 4 ? args[3] : "preprosseserte_avstander.txt";

        IO.println("Leser inn grafdata...");
        long innlastingStart = System.nanoTime();
        long filStart = System.nanoTime();
        List<Node> noder = Filinnleser.lesNoder(nodeFil);
        skrivTid("Innlasting av noder", System.nanoTime() - filStart);

        filStart = System.nanoTime();
        List<Kant> kanter = Filinnleser.lesKanter(kantFil, noder);
        skrivTid("Innlasting av kanter", System.nanoTime() - filStart);

        filStart = System.nanoTime();
        List<Interessepunkt> interessepunkter =
            Filinnleser.lesInteressepunkter(interessepunktFil, noder);
        skrivTid("Innlasting av interessepunkter", System.nanoTime() - filStart);
        skrivTid("Total filinnlasting", System.nanoTime() - innlastingStart);
        System.out.printf(
            Locale.ROOT, "Leste %,d noder, %,d kanter og %,d interessepunkter.%n",
            noder.size(), kanter.size(), interessepunkter.size()
        );

        kjørMeny(new Sesjon(noder, altFil), new Scanner(System.in));
}

private static void kjørMeny(Sesjon sesjon, Scanner input) throws IOException {
        while (true) {
                IO.println();
                IO.println("=== Veinavigasjon ===");
                IO.println("1. Finn rute");
                IO.println("2. Finn nærmeste destinasjon (Dijkstra)");
                IO.println("0. Avslutt");

                int valg = lesHeltall(input, "Velg: ");
                switch (valg) {
                        case 1 -> finnRute(input, sesjon);
                        case 2 -> finnDestinasjoner(input, sesjon.noder);
                        case 0 -> {
                                IO.println("Avslutter.");
                                return;
                        }
                        default -> IO.println("Ugyldig menyvalg.");
                }
        }
}

private static void finnRute(Scanner input, Sesjon sesjon) throws IOException {
        Node fra = lesNode(input, sesjon.noder, "Fra-nodeindeks: ");
        Node til = lesNode(input, sesjon.noder, "Til-nodeindeks: ");
        Algoritme algoritme = lesAlgoritme(input);

        ALTData altdata = algoritme == Algoritme.ALT
            ? sesjon.hentALTdata() : null;
        long start = System.nanoTime();
        Dijkstra dijkstra = new Dijkstra();
        ALT alt = new ALT();
        List<Node> rute = algoritme == Algoritme.DIJKSTRA
            ? dijkstra.dijkstraFraTil(sesjon.noder, fra, til)
            : alt.altFraTil(sesjon.noder, fra, til, altdata);
        long nanosekunder = System.nanoTime() - start;

        if (rute.isEmpty()) {
                IO.println("Fant ingen rute.");
                skrivAlgoritmetid(algoritme, nanosekunder);
                return;
        }

        long kjøretid = finnKjøretid(rute);
        System.out.printf(
            Locale.ROOT,
            "Fant rute med %,d noder. Kjøretid: %d:%02d:%02d,%02d.%n",
            rute.size(),
            kjøretid / 360000,
            (kjøretid / 6000) % 60,
            (kjøretid / 100) % 60,
            kjøretid % 100
        );
        skrivAlgoritmetid(algoritme, nanosekunder);
        System.out.printf("Noder hentet fra prioritetskøen: %d.%n",
            algoritme == Algoritme.DIJKSTRA
                ? dijkstra.hentSistProsesserteNoder()
                : alt.hentSistProsesserteNoder());

        Path fil = Path.of("rute.geojson");
        eksporterRute(fil, rute);
        IO.println("GeoJSON-rute skrevet til " + fil.toAbsolutePath());
}

private static void finnDestinasjoner(Scanner input, List<Node> noder)
    throws IOException {
        Node fra = lesNode(input, noder, "Start-nodeindeks: ");
        Set<Interessepunkttype> typer = lesDestinasjonstyper(input);
        int antall = lesPositivtHeltall(input, "Antall destinasjoner: ");

        long start = System.nanoTime();
        List<Node> treff = new Dijkstra()
            .finnXNærmesteInteressepunkter(noder, fra, typer, antall);
        long nanosekunder = System.nanoTime() - start;

        if (treff.isEmpty()) {
                IO.println("Fant ingen destinasjoner av denne typen.");
        } else {
                System.out.printf("Fant %d destinasjon(er):%n", treff.size());
                for (Node node : treff) {
                        skrivDestinasjon(node, typer);
                }
        }
        System.out.printf(
            Locale.ROOT, "Dijkstra brukte %.3f ms.%n", nanosekunder / 1_000_000.0
        );

        Path fil = Path.of("destinasjoner.geojson");
        eksporterDestinasjoner(fil, treff, typer);
        IO.println("GeoJSON-destinasjoner skrevet til " + fil.toAbsolutePath());
}

private static Node lesNode(Scanner input, List<Node> noder, String ledetekst) {
        while (true) {
                int nodeNr = lesHeltall(input, ledetekst);
                if (nodeNr >= 0 && nodeNr < noder.size()) {
                        return noder.get(nodeNr);
                }
                System.out.printf("Nodeindeks må være mellom 0 og %d.%n", noder.size() - 1);
        }
}

private static Set<Interessepunkttype> lesDestinasjonstyper(Scanner input) {
        Interessepunkttype[] typer = Interessepunkttype.values();
        IO.println("Destinasjonstype:");
        for (int i = 0; i < typer.length; i++) {
                System.out.printf("%d. %s%n", i + 1, typer[i]);
        }

        while (true) {
                IO.print("Velg type(r), kommaseparert (f.eks. 1,4,5): ");
                String[] valg = input.nextLine().split(",", -1);
                Set<Interessepunkttype> valgteTyper = EnumSet.noneOf(
                    Interessepunkttype.class
                );
                boolean gyldig = valg.length > 0;
                for (String tekst : valg) {
                        try {
                                int nummer = Integer.parseInt(tekst.strip());
                                if (nummer < 1 || nummer > typer.length) {
                                        gyldig = false;
                                        break;
                                }
                                valgteTyper.add(typer[nummer - 1]);
                        } catch (NumberFormatException e) {
                                gyldig = false;
                                break;
                        }
                }
                if (gyldig && !valgteTyper.isEmpty()) {
                        return valgteTyper;
                }
                IO.println("Ugyldig valg. Skriv ett eller flere kategorinummer, f.eks. 1,4,5.");
        }
}

private static Algoritme lesAlgoritme(Scanner input) {
        while (true) {
                IO.println("Algoritme:");
                IO.println("1. Dijkstra");
                IO.println("2. ALT");
                int valg = lesHeltall(input, "Velg algoritme: ");
                if (valg == 1) {
                        return Algoritme.DIJKSTRA;
                }
                if (valg == 2) {
                        return Algoritme.ALT;
                }
                IO.println("Ugyldig algoritmevalg.");
        }
}

private static int lesPositivtHeltall(Scanner input, String ledetekst) {
        while (true) {
                int tall = lesHeltall(input, ledetekst);
                if (tall > 0) {
                        return tall;
                }
                IO.println("Verdien må være større enn 0.");
        }
}

private static int lesHeltall(Scanner input, String ledetekst) {
        while (true) {
                IO.print(ledetekst);
                String linje = input.nextLine().strip();
                try {
                        return Integer.parseInt(linje);
                } catch (NumberFormatException e) {
                        IO.println("Skriv inn et heltall.");
                }
        }
}

private static long finnKjøretid(List<Node> rute) {
        long kjøretid = 0;
        for (int i = 1; i < rute.size(); i++) {
                Node fra = rute.get(i - 1);
                Node til = rute.get(i);
                int kortesteKant = Integer.MAX_VALUE;
                for (Kant kant : fra.kanter) {
                        if (kant.tilNode == til && kant.kjøretid < kortesteKant) {
                                kortesteKant = kant.kjøretid;
                        }
                }
                if (kortesteKant == Integer.MAX_VALUE) {
                        throw new IllegalStateException(
                            "Fant ikke kanten i ruterekonstruksjonen.");
                }
                kjøretid += kortesteKant;
        }
        return kjøretid;
}

private static void eksporterRute(Path fil, List<Node> rute) throws IOException {
        new GeoJsonEksportør().eksporterRute(fil, rute);
}

private static void eksporterDestinasjoner(Path fil, List<Node> treff,
    Set<Interessepunkttype> typer) throws IOException {
        new GeoJsonEksportør().eksporterDestinasjoner(fil, treff, typer);
}

private static void skrivDestinasjon(Node node, Set<Interessepunkttype> typer) {
        String navn = node.interessepunkter.stream()
            .filter(interessepunkt ->
                interessepunkt.typer.stream().anyMatch(typer::contains))
            .map(interessepunkt -> interessepunkt.navn)
            .findFirst()
            .orElse(beskrivDestinasjonstyper(typer));
        System.out.printf(
            Locale.ROOT, "%s (node %d), GPS %.7f, %.7f%n",
            navn, node.nodeNr, node.breddegrad, node.lengdegrad
        );
}

private static String beskrivDestinasjonstyper(Set<Interessepunkttype> typer) {
        return typer.stream()
            .map(Interessepunkttype::toString)
            .collect(java.util.stream.Collectors.joining(" eller "));
}

private static void skrivAlgoritmetid(Algoritme algoritme, long nanosekunder) {
        System.out.printf(
            Locale.ROOT, "%s brukte %.3f ms.%n",
            algoritme == Algoritme.DIJKSTRA ? "Dijkstra" : "ALT",
            nanosekunder / 1_000_000.0
        );
}

private static void skrivTid(String operasjon, long nanosekunder) {
        System.out.printf(
            Locale.ROOT, "%s: %.3f ms.%n", operasjon, nanosekunder / 1_000_000.0
        );
}
