package org.clin.panels;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Model {

  public static final String ITEMS_SEPARATOR = ",";

  private final List<String> headers = List.of("symbol", "panels", "version");
  private final Map<String, Panel> symbols = new TreeMap<>();

  public void add(String symbol, String panels, String versions) {
    symbols.computeIfAbsent(symbol, f -> new Panel());
    symbols.get(symbol).addPanels(panels);
    symbols.get(symbol).addVersions(versions);
  }

  public List<String> getDistinctPanels() {
    return symbols.values().stream().flatMap((panel) -> panel.panels.stream()).collect(Collectors.toList());
  }

  public List<String> getDistinctVersions() {
    return symbols.values().stream().flatMap((panel) -> panel.versions.stream()).collect(Collectors.toList());
  }

  @Data
  public static class Panel {

    private final Set<String> panels = new TreeSet<>();
    private final Set<String> versions = new TreeSet<>();

    public void addPanels(String panels) {
      this.panels.addAll(toList(panels));
    }

    public void addVersions(String versions) {
      this.versions.addAll(toList(versions));
    }

    private List<String> toList(String items) {
      if (StringUtils.isNotBlank(items)) {
        return Arrays.stream(items.split(ITEMS_SEPARATOR)).filter(StringUtils::isNotBlank).map(StringUtils::trim).toList();
      }
      return List.of();
    }
  }
}
