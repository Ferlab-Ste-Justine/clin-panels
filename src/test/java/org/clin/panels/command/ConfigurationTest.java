package org.clin.panels.command;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ConfigurationTest {

  final Configuration config = Configuration.load();

  @Test
  void shouldHaveValidConfig() {
    assertFalse(config.getDatalakeBucketName().isEmpty());
    assertFalse(config.getDatalakeFolderName().isEmpty());
    assertFalse(config.getPublicBucketName().isEmpty());
    assertFalse(config.getPublicFolderName().isEmpty());
    assertFalse(config.getPanels().isEmpty());
  }

  @Test
  void shouldComputePaths() {
    assertEquals("raw/landing/panels/panels.tsv", config.getDatalakePanelsPath());
    assertEquals("panels/panels.xlsx", config.getPublicPanelsPath());
  }

}