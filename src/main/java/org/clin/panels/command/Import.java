package org.clin.panels.command;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.clin.panels.Parser;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.util.concurrent.Callable;

@Slf4j
@Command(name = "Import", description = "Import from S3 the input Excel file and convert it into panels.tsv")
public class Import implements Callable<Integer> {

  @Option(names = {"-f", "--file"}, description = "Input Excel file to import", required = true)
  private String file;

  @Option(names = {"-v", "--validate"}, description = "Validate the input Excel file only (write nothing to S3)")
  private boolean validate = false;

  @Option(names = {"-d", "--debug"}, description = "Enable debug logs")
  private boolean debug = false;

  @Override
  public Integer call() {
    try {
      Logger.setDebug(debug);

      log.info("Start import Excel file: {}", file);

      var config = Configuration.load();
      var s3Client = new S3Client(config.getAws());

      var model = new ExcelBuilder(s3Client, config, file)
        .parseConfig()
        .checkS3FileExists()
        .parseS3Content()
        .validate()
        .build();

      log.info("Distinct panels: {}", model.getDistinctPanels());
      log.info("Distinct versions: {}", model.getDistinctVersions());

      var length = new PublishBuilder(s3Client, config, file, Parser.toTSV(model))
        .parseConfig()
        .build();

      log.info("Panels uploaded to datalake size: {}", FileUtils.byteCountToDisplaySize(length));
      log.info("Import completed with success");

      return 0;
    } catch (Exception e) {
      log.error("Failed to import", e);
      return 1;
    }
  }

  public static void main(String[] args){
    int exitCode = new CommandLine(new Import()).execute(args);
    System.exit(exitCode);
  }
}
