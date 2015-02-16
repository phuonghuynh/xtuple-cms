package com.xtuple.controller;

import com.xtuple.dto.CompanyInfo;
import com.xtuple.dto.Result;
import com.xtuple.entity.Company;
import com.xtuple.entity.User;
import com.xtuple.service.CompanyService;
import com.xtuple.task.InputStreamTask;
import com.xtuple.task.RunningTask;
import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;
import org.javalite.activejdbc.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

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

  @Value("${db.host}")
  private String dbHost;

  @Value("${db.username}")
  private String dbUsername;

  @Value("${db.password}")
  private String dbPassword;

  @Value("${xtuple.cmd.install}")
  private String installCmd;

  @Resource
  private CompanyService companyService;

  @ResponseBody
  @RequestMapping(value = "/company", method = RequestMethod.GET)
  public List<CompanyInfo> all() {
    List<CompanyInfo> companyInfos = new ArrayList<>();
    Base.open("org.postgresql.Driver", dbHost, dbUsername, dbPassword);
    List<Company> companies = Company.findAll();
    for (Company company : companies) {
      CompanyInfo companyInfo = CompanyInfo.CompanyInfoBuilder.companyInfo()
        .withInstallName(company.get("_installName").toString())
        .withPublicDomain(company.get("_publicDomain").toString())
        .withAdminPassword(company.get("_adminPassword").toString()).build();
      companyInfos.add(companyInfo);
    }
    Base.close();
    return companyInfos;
  }


  @MessageMapping("/user/company/register")
  public void register(CompanyInfo companyInfo) {
    final String destination = "/topic/" + companyInfo.getInstallName() + "/company/register";
    String command = String.format(installCmd, companyInfo.getInstallName(), companyInfo.getAdminPassword());
    try {
      String[] cmd = {"sh", "-c", "echo '" + sudoPwd + "'| sudo -S " + command};
      final Process process = Runtime.getRuntime().exec(cmd);
      Thread[] threads = new Thread[] {
        new InputStreamTask(process.getInputStream(), LOGGER, messagingTemplate, destination),
        new InputStreamTask(process.getErrorStream(), LOGGER, messagingTemplate, destination)
      };

      for (Thread t : threads) {
        t.start();
      }

      RunningTask warmTask = new RunningTask(">>System running tasks in the background...", LOGGER, messagingTemplate, destination);
      warmTask.start();

      for (Thread t : threads) {
        t.join();
      }

      LOGGER.debug("All threads stopped");
      warmTask.setStop(true);
      companyService.register(companyInfo);
      messagingTemplate.convertAndSend(destination, "ENDED!!!!");
    }
    catch (Exception e) {
      LOGGER.error("Error", e);
      messagingTemplate.convertAndSend(destination, "Error, see log file for details");
    }
  }
}
