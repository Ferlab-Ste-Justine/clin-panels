package org.clin.panels;

import lombok.Data;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Model {

  private List<String> headers;
  private Map<String, Panel> symbols;

  public Model(CSVParser parser) {
    this.headers = parser.getHeaderNames();
    this.symbols = new TreeMap<>();
    parser.stream().iterator().forEachRemaining((r) -> {
      symbols.put(r.get("symbol"), new Panel(r));
    });
  }

  public void add(String symbol, String panel, String version) {
    symbols.computeIfAbsent(symbol,f -> new Panel());
    symbols.get(symbol).panels.add(panel);
    symbols.get(symbol).versions.add(version);
  }

  public Set<String> getDistinctPanels() {
    return symbols.values().stream().flatMap((panel) -> panel.panels.stream()).collect(Collectors.toSet());
  }

  @Data
  public static class Panel {

    private Set<String> panels;
    private Set<String> versions;

    public Panel() {
      this.panels = new TreeSet<>();
      this.versions = new TreeSet<>();
    }

    public Panel(CSVRecord record) {
      this.panels = new TreeSet<>(Arrays.asList(record.get("panels").split(",")));
      this.versions = new TreeSet<>(Arrays.asList(record.get("version").split(",")));
    }
  }
}
