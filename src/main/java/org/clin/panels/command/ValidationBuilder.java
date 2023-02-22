package org.clin.panels.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.clin.panels.Model;
import org.clin.panels.Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class ValidationBuilder {

  private final S3Client s3Client;
  private final Configuration config;
  private final String file;
  private final Model model;

  private final Result result = new Result();

  public ValidationBuilder extractTimestamp() {
    try {
      this.result.timestamp = file.split("_")[2].replace(".xlsx", "");
    } catch (Exception e) {
      this.addError("Can't extract timestamp from excel file name: %s", file);
    }
    return this;
  }

  public ValidationBuilder checkModel() {
    this.result.currentModel = model;
    if (model.getDistinctPanels().isEmpty()) {
      this.addError("The model has no panels", file);
    }
    if (model.getDistinctVersions().isEmpty()) {
      this.addError("The model has no versions", file);
    }
    if (model.getSymbols().isEmpty()) {
      this.addError("The model has no symbols", file);
    }
    return this;
  }

  public ValidationBuilder compareWithPreviousModel() throws IOException {
    if (s3Client.exists(config.getDatalakeBucketName(), config.getPreviousPanelsPath())) {
      log.debug("Found previous panels in bucket: {} at location: {}", config.getDatalakeBucketName(), config.getPreviousPanelsPath());
      var previousContent = s3Client.getContent(config.getDatalakeBucketName(), config.getPreviousPanelsPath());
      var previousModel = Parser.load(previousContent);
      this.result.previousModel = previousModel;
      this.compareChanged(previousModel, model);
    } else {
      log.warn("No previous panels found in bucket: {} at location: {}", config.getDatalakeBucketName(), config.getPreviousPanelsPath());
    }
    return this;
  }

  private void compareChanged(Model previous, Model current) {
    // updated or new symbols
    for (String symbol: current.getSymbols().keySet()) {
      var currentPanels = current.getSymbols().get(symbol).getPanels();
      if (previous.getSymbols().containsKey(symbol)) {
        var previousPanels = previous.getSymbols().get(symbol).getPanels();
        if (!currentPanels.equals(previousPanels)) {
          this.addDiff(symbol, previousPanels, currentPanels);
        }
      } else {
        this.addDiff(symbol, null, currentPanels);
      }
    }
    // deleted symbols
    for (String symbol: previous.getSymbols().keySet()) {
      if (!current.getSymbols().containsKey(symbol)) {
        var previousPanels = previous.getSymbols().get(symbol).getPanels();
        this.addDiff(symbol, previousPanels, null);
      }
    }
  }

  private void addError(String msg, Object... params) {
    this.result.errors.add(String.format(msg, params));
  }

  private void addDiff(String symbol, Set<String> panelsBefore, Set<String> panelsAfter) {
    if (panelsAfter == null) {
      this.result.symbolDeletes.add(symbol);
    } else if (panelsBefore == null) {
      this.result.symbolNews.add(String.format("%s => %s", symbol, panelsAfter));
    } else {
      this.result.symbolUpdates.add(String.format("%s %s => %s", symbol, panelsBefore, panelsAfter));
    }
  }

  public Result build() {
    return this.result;
  }

  @Getter
  public static class Result {
    private final List<String> errors = new ArrayList<>();
    private final List<String> symbolNews = new ArrayList<>();
    private final List<String> symbolDeletes = new ArrayList<>();
    private final List<String> symbolUpdates = new ArrayList<>();
    private String timestamp;
    private Model currentModel;
    private Model previousModel;

    public boolean hasErrors() {
      return !errors.isEmpty();
    }

    public String buildSummary() {
      final StringBuilder builder = new StringBuilder();

      builder.append(formatSummary(currentModel, "Current")).append("\n");

      if (previousModel != null) {
        builder.append(formatSummary(previousModel, "Previous")).append("\n");
        builder.append("New symbols:\n");
        symbolNews.forEach((s) -> builder.append(s).append("\n"));
        builder.append("\n");
        builder.append("Updated symbols:\n");
        symbolUpdates.forEach((s) -> builder.append(s).append("\n"));
        builder.append("\n");
        builder.append("Deleted symbols:\n");
        symbolDeletes.forEach((s) -> builder.append(s).append("\n"));
        builder.append("\n");
      } else {
        builder.append("Previous model: not found\n\n");
      }

      return builder.toString();
    }

    private String formatSummary(Model model, String type) {
      return String.format("%s model:\n- Panels: %s\n- Versions: %s\n- Symbols: %s\n", type, model.getDistinctPanels(), model.getDistinctVersions(), model.getSymbols().size());
    }
  }
}
