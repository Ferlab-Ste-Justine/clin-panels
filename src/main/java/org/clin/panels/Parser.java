package org.clin.panels;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Parser {

  public static final CSVFormat TSV = CSVFormat.DEFAULT
    .withAllowDuplicateHeaderNames(true)
    .withDelimiter('\t')
    .withFirstRecordAsHeader()
    .withIgnoreEmptyLines()
    .withTrim();

  public static CSVParser parse(CSVFormat format, String path) throws IOException {
    return format.parse(Files.newBufferedReader(Paths.get(path), StandardCharsets.UTF_8));
  }

  public static CSVParser parse(CSVFormat format, byte[] content) throws IOException {
    return format.parse(new InputStreamReader(new ByteArrayInputStream(content), StandardCharsets.UTF_8));
  }

  private static Model load(CSVParser parser) {
    final Model model = new Model();
    parser.stream().iterator().forEachRemaining((r) -> {
      model.add(r.get("symbol"), r.get("panels"), r.get("version"));
    });
    IOUtils.closeQuietly(parser);
    return model;
  }

  public static Model load(String path) throws IOException {
    final CSVParser parser = parse(TSV, path);
    return load(parser);
  }

  public static Model load(byte[] content) throws IOException {
    final CSVParser parser = parse(TSV, content);
    return load(parser);
  }

  public static void write(Model model, String path) throws IOException {
    try (var buf = Files.newBufferedWriter(Paths.get(path), StandardCharsets.UTF_8)) {
      buf.write(toTSV(model));
    }
  }

  public static String toTSV(Model model) {
    final StringBuilder builder = new StringBuilder();
    builder.append(String.join("\t", model.getHeaders()));
    builder.append("\n");
    for (String symbol: model.getSymbols().keySet()){
      var panel = model.getSymbols().get(symbol);
      builder.append(String.format("%s\t%s\t%s\n", symbol, String.join(",", panel.getPanels()),String.join(",", panel.getVersions())));
    }
    return builder.toString();
  }
}
