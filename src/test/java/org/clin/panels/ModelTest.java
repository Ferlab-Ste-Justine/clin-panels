package org.clin.panels;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelTest {

  @Test
  void shouldHaveHeaders() {
    var model = new Model();
    assertEquals(List.of("symbol", "panels", "version"), model.getHeaders());
  }

  @Test
  void shouldHaveDistinctSymbols() {
    var model = new Model();
    model.add("s1", "RGDI", "RGDI_v1");
    model.add("s1", "RGDI", "RGDI_v1");
    assertEquals(1, model.getSymbols().size());
  }

  @Test
  void shouldAddSymbolIfNew() {
    var model = new Model();
    model.add("s1", "RGDI", "RGDI_v1");
    model.add("s2", "RGDI", "RGDI_v1");
    assertEquals(2, model.getSymbols().size());
  }

  @Test
  void shouldAddPanelToExistingSymbol() {
    var model = new Model();
    model.add("s1", "RGDI", "RGDI_v1");
    model.add("s1", "POLYM", "POLYM_v1");
    assertEquals(Set.of("RGDI", "POLYM"), model.getSymbols().get("s1").getPanels());
    assertEquals(Set.of("RGDI_v1", "POLYM_v1"), model.getSymbols().get("s1").getVersions());
  }

  @Test
  void shouldAddMultiplePanelsAndVersions() {
    var model = new Model();
    model.add("s1", "RGDI,POLYM", "RGDI_v1,POLYM_v1");
    assertEquals(Set.of("RGDI", "POLYM"), model.getSymbols().get("s1").getPanels());
    assertEquals(Set.of("RGDI_v1", "POLYM_v1"), model.getSymbols().get("s1").getVersions());
  }

  @Test
  void shouldIgnoreEmptyPanels() {
    var model = new Model();
    model.add("s1", ",", "");
    assertEquals(Set.of(), model.getDistinctPanels());
    assertEquals(Set.of(), model.getDistinctVersions());
  }

  @Test
  void shouldTrimPanelsAndVersions() {
    var model = new Model();
    model.add("s1", "RGDI ,  POLYM  ", "   RGDI_v1,POLYM_v1   ");
    assertEquals(Set.of("RGDI", "POLYM"), model.getSymbols().get("s1").getPanels());
    assertEquals(Set.of("RGDI_v1", "POLYM_v1"), model.getSymbols().get("s1").getVersions());
  }

  @Test
  void shouldBeRobust() {
    var model = new Model();
    model.add("s1", null, null);
    assertEquals(Set.of(), model.getDistinctPanels());
    assertEquals(Set.of(), model.getDistinctVersions());
  }

}