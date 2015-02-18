package com.xtuple.service.impl;

import com.xtuple.dto.CompanyInfo;
import com.xtuple.entity.Company;
import com.xtuple.service.CompanyService;
import org.javalite.activejdbc.Base;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by phuonghqh on 2/15/15.
 */
@Service
public class CompanyServiceImpl implements CompanyService {

  @Value("${db.host}")
  private String dbHost;

  @Value("${db.username}")
  private String dbUsername;

  @Value("${db.password}")
  private String dbPassword;

  public void register(CompanyInfo companyInfo) {
    Base.open("org.postgresql.Driver", dbHost, dbUsername, dbPassword);
    Company company = new Company();
    company.set(new String[]{"_installName", "_adminPassword", "_publicDomain", "_sysReport"},
      new String[]{companyInfo.getInstallName(), companyInfo.getAdminPassword(), companyInfo.getPublicDomain(), companyInfo.getSysReport()});
    company.saveIt();
    Base.close();
  }
}
