import java.util.List;

public class Main {
    void main(String[] args) {
        List<Node> noder = NodeKantInteressepunktInnleser.lesNoder(args[0]);
        List<Kant> kanter = NodeKantInteressepunktInnleser.lesKanter(args[1], noder);
        List<Interessepunkt> interessepunkter = NodeKantInteressepunktInnleser.lesInteressepunkter(args[2], noder);
        IO.println(interessepunkter.size());
        IO.println(interessepunkter.getLast());
    }
}
