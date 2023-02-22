package org.clin.panels.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.clin.panels.Model;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class ExcelBuilder {

  private final S3Client s3Client;
  private final Configuration config;
  private final String excelFileName;

  private String publicExcelPath;
  private Model model;

  public ExcelBuilder checkS3FileExists() throws FileNotFoundException {
    this.publicExcelPath = Paths.get(config.getPublicFolderName(), excelFileName).toString();
    if (!s3Client.exists(config.getPublicBucketName(), publicExcelPath)) {
      throw new FileNotFoundException(String.format("Bucket: %s file: %s", config.getPublicBucketName(), publicExcelPath));
    }
    return this;
  }

  public ExcelBuilder parseS3Content() throws IOException {
    var s3Content = s3Client.getContent(config.getPublicBucketName(), publicExcelPath);
    try (var bis = new ByteArrayInputStream(s3Content)) {
      Workbook workbook = new XSSFWorkbook(bis);
      var sheet = workbook.getSheetAt(0);
      var metadata = parseMetadata(sheet);
      this.model = parseModel(sheet, metadata);
    }
    return this;
  }

  private ExcelMetadata parseMetadata(Sheet sheet) {
    var metadata = new ExcelMetadata();

    var panelsRow = sheet.getRow(0);
    var versionsRow = sheet.getRow(3);

    for (int i = 0; i < panelsRow.getPhysicalNumberOfCells(); i++) {
      var value = parseValue(panelsRow.getCell(i));
      var version = parseValue(versionsRow.getCell(i));
      if ("gene".equalsIgnoreCase(value) && i != metadata.symbolIndex) {
        metadata.symbolIndex = i;
        log.warn("Symbol column found at index: {}", i);
      } else if (StringUtils.isNoneBlank(value, version) && version.toLowerCase().contains("v")) {
        var previousVersion = metadata.versions.getOrDefault(value, "");
        if (version.compareTo(previousVersion) > 0) {
          metadata.panels.put(value, i);
          metadata.versions.put(value, version);
          log.debug("Panel detected: {} with version: {} (previous version: {})", value, version, previousVersion);
        }
      }
    }

    return metadata;
  }

  private Model parseModel(Sheet sheet, ExcelMetadata metadata) {
    var model = new Model();

    for (int i = 5; i < sheet.getPhysicalNumberOfRows(); i++) {
      var row = sheet.getRow(i);
      if (row != null) {
        var symbol = parseValue(row.getCell(metadata.symbolIndex));
        if (StringUtils.isNotBlank(symbol)) {
          for (String panel : metadata.panels.keySet()) {
            var panelIndex = metadata.panels.get(panel);
            var yes = "Y".equalsIgnoreCase(parseValue(row.getCell(panelIndex)));
            if (yes) {
              var version = metadata.versions.get(panel);
              model.add(symbol, panel, String.format("%s_%s", panel, version));
              log.debug("Symbol: {} has panel: {} for version: {}", symbol, panel, version);
            }
          }
        }
      }
    }
    return model;
  }

  public Excel build() {
    return new Excel(model, publicExcelPath);
  }

  private String parseValue(Cell cell) {
    try {
      return cell.getStringCellValue().split("\n")[0].trim();
    } catch (Exception e) {
      return "";
    }
  }

  public static class ExcelMetadata {
    private Integer symbolIndex = 0;  // index of the symbol column
    private final Map<String, Integer> panels = new HashMap<>(); // panel -> column index
    private final Map<String, String> versions = new HashMap<>(); // panel -> version
  }

  @Getter
  @RequiredArgsConstructor
  public static class Excel {
    private final Model model;
    private final String s3Path;
  }
}
