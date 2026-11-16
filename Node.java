public class Node {
    int nodeNr;
    double breddegrad;
    double lengdegrad;

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
