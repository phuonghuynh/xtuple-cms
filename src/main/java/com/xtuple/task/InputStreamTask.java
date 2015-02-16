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

  private BufferedReader inputStream;

  private Logger logger;

  private SimpMessagingTemplate messagingTemplate;

  private String destination;

  public InputStreamTask(InputStream inputStream, Logger logger, SimpMessagingTemplate messagingTemplate, String destination) {
    this.inputStream = new BufferedReader(new InputStreamReader(inputStream));
    this.logger = logger;
    this.messagingTemplate = messagingTemplate;
    this.destination = destination;
  }

  public void run() {
    try {
      String line;
      while ((line = inputStream.readLine()) != null) {
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
