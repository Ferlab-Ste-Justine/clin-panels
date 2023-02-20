package org.clin.panels;

import com.typesafe.config.ConfigFactory;
import picocli.CommandLine;
import picocli.CommandLine.*;

import java.util.concurrent.Callable;

@Command(name = "Import", description = "Import from S3 the input Excel file and convert it into panels.tsv")
public class Import implements Callable<Integer> {

  @Option(names = {"-f", "--file"}, description = "Input Excel file to import", required = true)
  private String file;

  @Override
  public Integer call() {
    System.out.println(file);

    var config = ConfigFactory.load();
    var awsConfig = config.getConfig("aws");
    System.out.println(awsConfig.getString("access-key"));

    return 0;
  }

  public static void main(String[] args){
    int exitCode = new CommandLine(new Import()).execute(args);
    System.exit(exitCode);
  }

}
