package org.clin.panels;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class ModelTest {

  final CSVParser parser = Mockito.mock(CSVParser.class);

  @BeforeEach
  void beforeEach() {
    when(parser.getHeaderNames()).thenReturn(List.of("symbol", "panels", "version"));
  }

  @Test
  void shouldHaveHeaders() {
    var model = new Model(parser);
    assertEquals(List.of("symbol", "panels", "version"), model.getHeaders());
  }

  @Test
  void shouldExtractModelFromTSV() {
    var records = List.of(buildRecord("s1", "RGDI", "RDGI_v1"), buildRecord("s2", "POLYM", "POLYM_v1"));
    when(parser.stream()).thenReturn(records.stream());
    var model = new Model(parser);
    assertEquals(Set.of("POLYM", "RGDI"), model.getDistinctPanels());
    assertEquals(2, model.getSymbols().size());
  }

  @Test
  void shouldHaveDistinctSymbols() {
    var model = new Model(parser);
    model.add("s1", "RGDI", "RDGI_v1");
    model.add("s1", "RGDI", "RDGI_v1");
    assertEquals(Set.of("RGDI"), model.getSymbols().get("s1").getPanels());
    assertEquals(Set.of("RDGI_v1"), model.getSymbols().get("s1").getVersions());
    assertEquals(1, model.getSymbols().size());
  }

  @Test
  void shouldAddPanelIfNew() {
    var model = new Model(parser);
    model.add("s1", "RGDI", "RDGI_v1");
    model.add("s2", "RGDI", "RDGI_v1");
    assertEquals(2, model.getSymbols().size());
  }

  @Test
  void shouldAddPanelToExistingSymbol() {
    var model = new Model(parser);
    model.add("s1", "RGDI", "RDGI_v1");
    model.add("s1", "POLYM", "POLYM_v1");
    assertEquals(Set.of("RGDI", "POLYM"), model.getSymbols().get("s1").getPanels());
    assertEquals(Set.of("RDGI_v1", "POLYM_v1"), model.getSymbols().get("s1").getVersions());
  }

  private CSVRecord buildRecord(String symbol, String panels, String version) {
    var record = Mockito.mock(CSVRecord.class);
    when(record.get(eq("symbol"))).thenReturn(symbol);
    when(record.get(eq("panels"))).thenReturn(panels);
    when(record.get(eq("version"))).thenReturn(version);
    return record;
  }

}