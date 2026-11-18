import java.util.ArrayList;
import java.util.List;

public class Node {
        int nodeNr;
        double breddegrad;
        double lengdegrad;
        List<Kant> kanter = new ArrayList<>();
        List<Interessepunkt> interessepunkter = new ArrayList<>();

        public Node(int nodeNr, double breddegrad, double lengdegrad) {
                this.nodeNr = nodeNr;
                this.breddegrad = breddegrad;
                this.lengdegrad = lengdegrad;
        }

        @Override
        public String toString() {
                return "nodeNr: " + nodeNr + " - bredde:" + breddegrad + " - lengde:" + lengdegrad;
        }
}
