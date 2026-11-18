/**
 * Denne filen er fullstendig generert av Microsoft Copilot.
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class GeoJsonEksportør {
        public void eksporterRute(Path fil, List<Node> rute) throws IOException {
                Objects.requireNonNull(fil, "fil");
                Objects.requireNonNull(rute, "rute");
                if (rute.isEmpty()) {
                        throw new IllegalArgumentException("Kan ikke eksportere en tom rute.");
                }

                StringBuilder geoJson = new StringBuilder();
                geoJson.append("{\n")
                    .append("  \"type\": \"FeatureCollection\",\n")
                    .append("  \"features\": [{\n")
                    .append("    \"type\": \"Feature\",\n")
                    .append("    \"properties\": {\"name\": \"Rute\"},\n")
                    .append("    \"geometry\": {\n");

                if (rute.size() == 1) {
                        geoJson.append("      \"type\": \"Point\",\n")
                            .append("      \"coordinates\": ");
                        appendCoordinates(geoJson, rute.get(0));
                        geoJson.append('\n');
                } else {
                        geoJson.append("      \"type\": \"LineString\",\n")
                            .append("      \"coordinates\": [");
                        for (int i = 0; i < rute.size(); i++) {
                                if (i > 0) {
                                        geoJson.append(", ");
                                }
                                appendCoordinates(geoJson, rute.get(i));
                        }
                        geoJson.append("]\n");
                }

                geoJson.append("    }\n")
                    .append("  }]\n")
                    .append("}\n");
                Files.writeString(fil, geoJson, StandardCharsets.UTF_8);
        }

        public void eksporterDestinasjoner(Path fil, List<Node> destinasjoner,
            Interessepunkttype type) throws IOException {
                Objects.requireNonNull(fil, "fil");
                Objects.requireNonNull(destinasjoner, "destinasjoner");
                Objects.requireNonNull(type, "type");

                StringBuilder geoJson = new StringBuilder();
                geoJson.append("{\n  \"type\": \"FeatureCollection\",\n  \"features\": [");

                for (int i = 0; i < destinasjoner.size(); i++) {
                        Node node = Objects.requireNonNull(destinasjoner.get(i), "destinasjonsnode");
                        String navn = node.interessepunkter.stream()
                            .filter(interessepunkt -> interessepunkt.typer.contains(type))
                            .map(interessepunkt -> interessepunkt.navn)
                            .findFirst()
                            .orElse(type.toString());

                        geoJson.append(i == 0 ? "\n" : ",\n")
                            .append("    {\n")
                            .append("      \"type\": \"Feature\",\n")
                            .append("      \"properties\": {\n")
                            .append("        \"name\": \"")
                            .append(jsonEscape(navn))
                            .append("\",\n")
                            .append("        \"node_id\": ")
                            .append(node.nodeNr)
                            .append(",\n")
                            .append("        \"destination_type\": \"")
                            .append(jsonEscape(type.toString()))
                            .append("\"\n")
                            .append("      },\n")
                            .append("      \"geometry\": {\n")
                            .append("        \"type\": \"Point\",\n")
                            .append("        \"coordinates\": ");
                        appendCoordinates(geoJson, node);
                        geoJson.append("\n      }\n")
                            .append("    }");
                }

                if (!destinasjoner.isEmpty()) {
                        geoJson.append('\n');
                }
                geoJson.append("  ]\n}\n");
                Files.writeString(fil, geoJson, StandardCharsets.UTF_8);
        }

        private void appendCoordinates(StringBuilder geoJson, Node node) {
                Objects.requireNonNull(node, "node");
                if (!Double.isFinite(node.lengdegrad) || !Double.isFinite(node.breddegrad)
                    || node.lengdegrad < -180 || node.lengdegrad > 180
                    || node.breddegrad < -90 || node.breddegrad > 90) {
                        throw new IllegalArgumentException(
                            "Ugyldige GPS-koordinater for node " + node.nodeNr
                        );
                }
                geoJson.append(String.format(
                    Locale.ROOT, "[%.7f, %.7f]", node.lengdegrad, node.breddegrad
                ));
        }

        private String jsonEscape(String tekst) {
                StringBuilder escaped = new StringBuilder();
                for (int i = 0; i < tekst.length(); i++) {
                        char tegn = tekst.charAt(i);
                        switch (tegn) {
                                case '"' -> escaped.append("\\\"");
                                case '\\' -> escaped.append("\\\\");
                                case '\b' -> escaped.append("\\b");
                                case '\f' -> escaped.append("\\f");
                                case '\n' -> escaped.append("\\n");
                                case '\r' -> escaped.append("\\r");
                                case '\t' -> escaped.append("\\t");
                                default -> {
                                        if (tegn < 0x20) {
                                                escaped.append(String.format(
                                                    Locale.ROOT, "\\u%04x", (int) tegn
                                                ));
                                        } else {
                                                escaped.append(tegn);
                                        }
                                }
                        }
                }
                return escaped.toString();
        }
}
