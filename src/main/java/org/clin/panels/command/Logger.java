package org.clin.panels.command;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;

public class Logger {

  public static void setDebug(boolean debug) {
    final ch.qos.logback.classic.Logger root =(ch.qos.logback.classic.Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    if (debug) {
      root.setLevel(Level.DEBUG);
    }
  }

}
