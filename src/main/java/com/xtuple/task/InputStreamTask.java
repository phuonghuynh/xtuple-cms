package com.xtuple.task;

import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by phuonghqh on 2/15/15.
 */
public class InputStreamTask extends Thread {

  private StringBuilder sysReportBuilder;

  private BufferedReader inputStream;

  private Logger logger;

  private SimpMessagingTemplate messagingTemplate;

  private String destination;

  public InputStreamTask(InputStream inputStream, Logger logger,
                         SimpMessagingTemplate messagingTemplate, String destination, StringBuilder sysReportBuilder) {
    this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
    this.logger = logger;
    this.messagingTemplate = messagingTemplate;
    this.destination = destination;
    this.sysReportBuilder = sysReportBuilder;
  }

  public void run() {
    try {
      String line;
      while ((line = inputStream.readLine()) != null) {
        if (line.contains("sys-report")) {
          sysReportBuilder.append(line);
        }
        logger.debug(line);
        messagingTemplate.convertAndSend(destination, line);
      }
      logger.debug("Closing stream");
      inputStream.close();
    }
    catch (Exception e) {
      logger.error("Error", e);
    }
  }
}
