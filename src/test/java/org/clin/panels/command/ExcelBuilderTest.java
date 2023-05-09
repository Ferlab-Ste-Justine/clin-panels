package org.clin.panels.command;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ExcelBuilderTest {

  final S3Client s3Client = Mockito.mock(S3Client.class);
  final Configuration configuration = Configuration.load();

  @BeforeEach
  void beforeEach() {
    when(s3Client.exists(any(), any())).thenReturn(true);
  }

  @Test
  void shouldCheckIfExistsInS3() throws FileNotFoundException {
    new ExcelBuilder(s3Client, configuration, "panels_RQDM_timestamp.xlsx")
      .checkS3FileExists();
    verify(s3Client).exists(eq("cqgc-qa-app-public"), eq("panels/panels_RQDM_timestamp.xlsx"));
  }

  @Test
  void shouldParseS3Content() throws IOException {
    var content = IOUtils.toByteArray(getClass().getClassLoader().getResourceAsStream("panels.xlsx"));
    when(s3Client.getContent(any(), any())).thenReturn(content);
    final var excel = new ExcelBuilder(s3Client, configuration, null)
      .parseS3Content()
      .build();
    assertEquals(Set.of("MMG", "RGDI"), excel.getModel().getDistinctPanels());
    assertEquals(Set.of("MMG_v1", "RGDI_v2"), excel.getModel().getDistinctVersions());
    assertEquals(3, excel.getModel().getSymbols().size());
  }

}