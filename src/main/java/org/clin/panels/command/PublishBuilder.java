package org.clin.panels.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clin.panels.Model;
import org.clin.panels.Parser;

import java.nio.file.Paths;

@Slf4j
@RequiredArgsConstructor
public class PublishBuilder {

  private final S3Client s3Client;
  private final Configuration config;
  private final String file;
  private final String content;

  private String datalakeBucketName;
  private String datalakeFolderName;
  private String datalakeFileName;
  private String timestamp;

  public PublishBuilder parseConfig() {
    this.datalakeBucketName = config.getAws().getString("datalake-bucket-name");
    this.datalakeFolderName = config.getPanels().getString("datalake-bucket-folder");
    this.datalakeFileName = config.getPanels().getString("datalake-file-name");
    this.timestamp = file.split("_")[1].replace(".xlsx", "");
    return this;
  }

  public int build() {

    var pathWithTimestamp = Paths.get(datalakeFolderName, String.format("panels_%s.tsv", timestamp)).toString();
    var path = Paths.get(datalakeFolderName, datalakeFileName).toString();

    s3Client.writeContent(datalakeBucketName, pathWithTimestamp, content);
    s3Client.writeContent(datalakeBucketName, path, content);
    log.debug("Publish panels bucket: {} file: {}", datalakeBucketName, pathWithTimestamp);
    log.debug("Publish panels bucket: {} file: {}", datalakeBucketName, path);

    return content.length();
  }
}
