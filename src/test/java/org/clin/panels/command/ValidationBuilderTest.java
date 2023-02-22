package org.clin.panels.command;

import org.clin.panels.Model;
import org.clin.panels.Parser;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ValidationBuilderTest {

  final S3Client s3Client = Mockito.mock(S3Client.class);
  final Configuration configuration = Configuration.load();

  @Test
  void shouldValidateEmptyModel(){
    final var model = new Model();
    final var validation = new ValidationBuilder(null, null, null, model)
      .checkModel()
      .build();
    assertTrue(validation.hasErrors());
    assertEquals("The model has no panels", validation.getErrors().get(0));
    assertEquals("The model has no versions", validation.getErrors().get(1));
    assertEquals("The model has no symbols", validation.getErrors().get(2));
  }

  @Test
  void shouldValidateNonEmptyModel(){
    final var model = new Model();
    model.add("s1", "p1,p2", "v1,v2");
    final var validation = new ValidationBuilder(null, null, null, model)
      .checkModel()
      .build();
    assertFalse(validation.hasErrors());
  }

  @Test
  void shouldExtractTimestamp(){
    final var validation = new ValidationBuilder(null, null, "foo_RQDM_timestamp.xlsx", null)
      .extractTimestamp()
      .build();
    assertEquals("timestamp", validation.getTimestamp());
  }

  @Test
  void shouldCompareWithPreviousModel() throws IOException {
    final var model = new Model();
    model.add("s1", "p1,p2", "v1,v2");
    model.add("s2", "p1", "v1");

    final var previous = new Model();
    previous.add("s1", "p1", "v1");
    previous.add("s3", "p1", "v1");

    when(s3Client.exists(any(), any())).thenReturn(true);
    when(s3Client.getContent(any(), any())).thenReturn(Parser.toTSV(previous).getBytes());

    final var validation = new ValidationBuilder(s3Client, configuration, "foo.xlsx", model)
      .checkModel()
      .compareWithPreviousModel()
      .build();

    assertEquals("s2 => [p1]", validation.getSymbolNews().get(0));
    assertEquals("s1 [p1] => [p1, p2]", validation.getSymbolUpdates().get(0));
    assertEquals("s3", validation.getSymbolDeletes().get(0));

    assertFalse(validation.buildSummary().isEmpty());
  }

}