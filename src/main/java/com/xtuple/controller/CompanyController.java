package com.xtuple.controller;

import com.xtuple.dto.CompanyInfo;
import com.xtuple.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by phuonghqh on 2/15/15.
 */
@Controller
public class CompanyController {

  private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

  @Resource
  private SimpMessagingTemplate messagingTemplate;

  @Value("${sudo.pwd}")
  private String sudoPwd;


  @Value("${xtuple.cmd.install}")
  private String installCmd;

  @Resource
  private CompanyService companyService;

  //  @SendToUser("/queue/info")
//  @MessageMapping("/user/findByKey")
  @MessageMapping("/user/company/register")
  public void register(CompanyInfo companyInfo) {
    String destination = "/topic/" + companyInfo.getAdmin() + "/company/register";
    try {
      String[] cmd = {"/bin/bash", "-c", "echo '" + sudoPwd + "'| sudo -S ls"};
      Process process = Runtime.getRuntime().exec(cmd);
      BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
      for (String line = input.readLine(); line != null; line = input.readLine()) {
//        ssh user@host <<'ENDSSH'
//        #commands to run on remote host
//        ENDSSH
        messagingTemplate.convertAndSend(destination, "467 info sys-report \u001B[32mxTuple Instance: \u001B[39m");
//        Thread.sleep(5000);
      }
      input.close();
      companyService.register(companyInfo);
      messagingTemplate.convertAndSend(destination, "ENDED!!!!");
    }
    catch (Exception e) {
      LOGGER.error("Error", e);
      messagingTemplate.convertAndSend(destination, "Error, see log file for details");
    }
  }
}
