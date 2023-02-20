package org.clin.panels;

import java.io.IOException;

@Deprecated
public class Manual {

  /*
    This is the legacy way to build panels.tsv file
    the code remains here to keep trace of how it worked
    Import.java should be used with Airflow instead.
   */
  public static void main(String[]args) throws Exception {
    generate20230203();
    generate20230214();
  }

  private static void generate20230203() throws IOException {
    var model = Parser.parse("./data/output/panels_20230202.tsv");

    var rgdiPlus = Parser.parse(Parser.TSV, "./data/input/RGDI+.tsv");
    var rgdiV2 = Parser.parse(Parser.TSV, "./data/input/RGDI_v2.tsv");

    rgdiPlus.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get(0), "RGDI+", "RGDI+_v1");
    });

    rgdiV2.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get(0), "RGDI", "RGDI_v2");
    });

    System.out.println(model.getDistinctPanels());

    Parser.write(model, "./data/output/panels_20230203.tsv");
  }

  private static void generate20230214() throws IOException {
    var model = Parser.parse("./data/output/panels_20230203.tsv");

    var polymV1 = Parser.parse(Parser.TSV, "./data/input/POLYM_v1.tsv");

    polymV1.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get(0), "POLYM", "POLYM_v1");
    });

    System.out.println(model.getDistinctPanels());

    Parser.write(model, "./data/output/panels_20230214.tsv");
  }
}
