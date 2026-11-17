import java.util.EnumSet;

public class Interessepunkt {
        Node node;
        String navn;
        EnumSet<Interessepunkttype> typer = EnumSet.noneOf(Interessepunkttype.class);

        public Interessepunkt(Node node, int kode, String navn) {
                this.node = node;
                this.navn = navn;
                addType(kode);
        }

        void addType(int kode) {
                if ((kode & 1) == 1) typer.add(Interessepunkttype.STEDSNAVN);
                if ((kode & 2) == 2) typer.add(Interessepunkttype.BENSINSTASJON);
                if ((kode & 4) == 4) typer.add(Interessepunkttype.LADESTASJON);
                if ((kode & 8) == 8) typer.add(Interessepunkttype.SPISESTED);
                if ((kode & 16) == 16) typer.add(Interessepunkttype.DRIKKESTED);
                if ((kode & 32) == 32) typer.add(Interessepunkttype.OVERNATTINGSTED);
        }

        @Override
        public String toString() {
                StringBuilder interessepunktstreng = new StringBuilder();
                interessepunktstreng.append(node).append(" - navn: ").append(this.navn).append(" - typer: ");
                for (Interessepunkttype type : typer) {
                        interessepunktstreng.append(type).append(" ");
                }
                return interessepunktstreng.toString();
        }
}
