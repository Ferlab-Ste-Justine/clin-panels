package org.clin.panels.command;

import org.clin.panels.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PublishBuilderTest {

  final S3Client s3Client = Mockito.mock(S3Client.class);
  final Configuration configuration = Configuration.load();
  final ExcelBuilder.Excel excel = Mockito.mock(ExcelBuilder.Excel.class);

  @BeforeEach
  void beforeEach() {
    when(excel.getModel()).thenReturn(new Model());
    when(excel.getPublicExcelPath()).thenReturn("panels/panels_RQDM_timestamp.xlsx");
  }

  @Test
  void shouldCopyToDatalake(){
    new PublishBuilder(s3Client, configuration, "timestamp", excel)
      .copyToDatalake();
    verify(s3Client).writeContent(eq("cqgc-qa-app-datalake"), eq("raw/landing/panels/panels_RQDM_timestamp.tsv"), any());
  }

  @Test
  void shouldReleaseToPublic(){
    new PublishBuilder(s3Client, configuration, "timestamp", excel)
      .releasePublic();
    verify(s3Client).copyObject(eq("cqgc-qa-app-public"), eq("panels/panels_RQDM_timestamp.xlsx"),
      eq("cqgc-qa-app-public"), eq("panels/panels.xlsx"));
  }

}