package org.clin.panels.command;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class Configuration {

  private final Config aws;
  private final Config panels;

  private Configuration(Config aws, Config panels) {
    this.aws = aws;
    this.panels = panels;
    log.debug("Configuration (aws): {}", aws.toString());
    log.debug("Configuration (panels): {}", panels.toString());
  }

  public static Configuration load() {
    var config = ConfigFactory.load();
    return new Configuration(config.getConfig("aws"), config.getConfig("panels"));
  }
}
