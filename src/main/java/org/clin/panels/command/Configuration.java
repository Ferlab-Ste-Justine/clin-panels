package org.clin.panels.command;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Paths;

@Slf4j
@Getter
public class Configuration {

  private final Config aws;
  private final Config panels;

  private final String datalakeBucketName;
  private final String datalakeFolderName;


  private final String publicBucketName;
  private final String publicFolderName;

  private final String panelsFileName;
  private final String previousPanelsPath;

  private Configuration(Config aws, Config panels) {
    this.aws = aws;
    this.panels = panels;
    log.debug("Configuration (aws): {}", aws.toString());
    log.debug("Configuration (panels): {}", panels.toString());

    this.datalakeBucketName = aws.getString("datalake-bucket-name");
    this.datalakeFolderName = panels.getString("datalake-bucket-folder");

    this.publicBucketName = aws.getString("public-bucket-name");
    this.publicFolderName = panels.getString("public-bucket-folder");

    this.panelsFileName = panels.getString("file-name");
    this.previousPanelsPath = Paths.get(datalakeFolderName, panelsFileName + ".tsv").toString();
  }

  public static Configuration load() {
    var config = ConfigFactory.load();
    return new Configuration(config.getConfig("aws"), config.getConfig("panels"));
  }
}
