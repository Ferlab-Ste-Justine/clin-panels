package org.clin.panels.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clin.panels.Parser;

import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
public class PublishBuilder {

  private final S3Client s3Client;
  private final Configuration config;
  private final String timestamp;
  private final ExcelBuilder.Excel excel;

  private String content;

  public PublishBuilder copyToDatalake() {

    this.content = Parser.toTSV(excel.getModel());

    var pathWithTimestamp = Paths.get(config.getDatalakeFolderName(), String.format("panels_RQDM_%s.tsv", timestamp)).toString();

    s3Client.writeContent(config.getDatalakeBucketName(), pathWithTimestamp, content);
    s3Client.writeContent(config.getDatalakeBucketName(), config.getDatalakePanelsPath(), content);

    log.debug("Copy to datalake bucket: {} file: {}", config.getDatalakeBucketName(), pathWithTimestamp);
    log.debug("Copy to datalake bucket: {} file: {}", config.getDatalakeBucketName(),  config.getDatalakePanelsPath());
    return this;
  }

  public PublishBuilder releasePublic() {
    s3Client.copyObject(config.getPublicBucketName(), excel.getPublicExcelPath(), config.getPublicBucketName(), config.getPublicPanelsPath());
    log.debug("Release public bucket: {} file: {}", config.getPublicBucketName(), config.getPublicPanelsPath());
    return this;
  }

  public int build() {
    return content.length();
  }
}
