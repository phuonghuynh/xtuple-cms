package com.xtuple.task;

import org.slf4j.Logger;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by phuonghqh on 2/16/15.
 */
public class RunningTask extends Thread {

  private Logger logger;

  private SimpMessagingTemplate messagingTemplate;

  private String destination;

  private String message;

  private boolean stop = false;

  private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss zzz");

  public RunningTask(String message, Logger logger, SimpMessagingTemplate messagingTemplate, String destination) {
    this.message = message;
    this.logger = logger;
    this.messagingTemplate = messagingTemplate;
    this.destination = destination;
  }

  public void run() {
    while (!stop) {
      logger.debug(message);
      messagingTemplate.convertAndSend(destination,
        String.format("%s, current time: %s", message, dateFormat.format(Calendar.getInstance().getTime())));
      try {
        Thread.sleep(3000);
      }
      catch (InterruptedException e) {
        logger.error("Error", e);
      }
    }
  }

  public boolean isStop() {
    return stop;
  }

  public void setStop(boolean stop) {
    this.stop = stop;
  }
}