package com.xtuple.dto;

/**
 * Created by phuonghqh on 2/15/15.
 */
public class CompanyInfo {
  private String admin;

  private String password;

  private String domainName;

  public String getAdmin() {
    return admin;
  }

  public void setAdmin(String admin) {
    this.admin = admin;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDomainName() {
    return domainName;
  }

  public void setDomainName(String domainName) {
    this.domainName = domainName;
  }
}
