public class Kant {
        Node fraNode;
        Node tilNode;
        int kjøretid;
        int lengde;
        int fartsgrense;

        public Kant(Node fraNode, Node tilNode, int kjøretid, int lengde, int fartsgrense) {
                this.fraNode = fraNode;
                this.tilNode = tilNode;
                this.kjøretid = kjøretid; // 1/100s
                this.lengde = lengde; // meter
                this.fartsgrense = fartsgrense; // km/t
        }

        @Override
        public String toString() {
                return " fra: " + fraNode.nodeNr + " - til: " + tilNode.nodeNr + " - kjøretid: " + kjøretid + " - lengde: " + lengde + " - fartsgrense: " + fartsgrense;
        }
}
