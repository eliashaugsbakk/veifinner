void main(String[] args) {
        List<Node> noder = Filinnleser.lesNoder(args[0]);
        List<Kant> kanter = Filinnleser.lesKanter(args[1], noder);
        List<Interessepunkt> interessepunkter =
            Filinnleser.lesInteressepunkter(args[2], noder);
        new ALTdata(noder, "preprosseserte_avstander.txt");
}
