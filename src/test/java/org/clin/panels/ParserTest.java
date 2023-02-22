package org.clin.panels;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParserTest {

  @Test
  void toTSV() {
    final var tsv = Parser.toTSV(buildModel());
    assertEquals("symbol\tpanels\tversion\ns1\tp1,p2\tv1,v2\ns2\tp3\tv1\n", tsv);
  }

  @Test
  void load() throws IOException {
    byte[] content = Parser.toTSV(buildModel()).getBytes();
    final var res = Parser.load(content);
    assertEquals(2, res.getSymbols().size());
  }

  private Model buildModel() {
    final var model = new Model();
    model.add("s1", "p1,p2", "v1,v2");
    model.add("s2", "p3", "v1");
    return model;
  }
}