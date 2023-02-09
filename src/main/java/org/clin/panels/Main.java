package org.clin.panels;

public class Main {

  public static void main(String[]args) throws Exception {
    var model = Parser.parse("./data/output/panels_20230202.tsv");

    var rgdiPlus = Parser.parse(Parser.TSV, "./data/input/RGDI+ - Sheet1.tsv");
    var rgdiV2 = Parser.parse(Parser.TSV, "./data/input/RGDI_v2 - Sheet1.tsv");

    rgdiPlus.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get(0), "RGDI+", "RGDI+_v1");
    });

    rgdiV2.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get(0), "RGDI", "RGDI_v2");
    });

    System.out.println(model.getDistinctPanels());

    Parser.write(model, "./data/output/panels_20230203.tsv");

  }
}
