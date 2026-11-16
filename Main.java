import java.util.List;

public class Main {
    void main(String[] args) {
        List<Node> noder = NodeKantInnleser.lesNoder(args[0]);
        List<Kant> kanter = NodeKantInnleser.lesKanter(args[1], noder);
    }
}
