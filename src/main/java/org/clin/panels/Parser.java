package org.clin.panels;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Parser {

  static final CSVFormat TSV = CSVFormat.DEFAULT
    .withAllowDuplicateHeaderNames(true)
    .withDelimiter('\t')
    .withFirstRecordAsHeader()
    .withIgnoreEmptyLines()
    .withTrim();

  public static CSVParser parse(CSVFormat format, String path) throws IOException {
    return format.parse(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));
  }

  public static Model parse(String path) throws IOException {
    return new Model(parse(TSV, path));
  }

  public static void write(Model model, String path) throws IOException {
    var buf = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8);
    buf.write(String.join("\t", model.getHeaders()));
    buf.write("\n");
    for (String symbol: model.getSymbols().keySet()){
      var panel = model.getSymbols().get(symbol);
      buf.write(String.format("%s\t%s\t%s\n", symbol, String.join(",", panel.getPanels()),String.join(",", panel.getVersions())));
    }
    buf.close();
  }
}
