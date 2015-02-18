package com.xtuple.dto;

/**
 * Created by phuonghqh on 2/15/15.
 */
public class CompanyInfo {
  private String installName;

  private String adminPassword;

  private String publicDomain;

  private String sysReport;

  public String getSysReport() {
    return sysReport;
  }

  public void setSysReport(String sysReport) {
    this.sysReport = sysReport;
  }

  public String getInstallName() {
    return installName;
  }

  public void setInstallName(String installName) {
    this.installName = installName;
  }

  public String getAdminPassword() {
    return adminPassword;
  }

  public void setAdminPassword(String adminPassword) {
    this.adminPassword = adminPassword;
  }

  public String getPublicDomain() {
    return publicDomain;
  }

  public void setPublicDomain(String publicDomain) {
    this.publicDomain = publicDomain;
  }

  public static class CompanyInfoBuilder
    {
        private CompanyInfo companyInfo;

        private CompanyInfoBuilder()
        {
            companyInfo = new CompanyInfo();
        }

        public CompanyInfoBuilder withInstallName(String installName)
        {
            companyInfo.installName = installName;
            return this;
        }

        public CompanyInfoBuilder withAdminPassword(String adminPassword)
        {
            companyInfo.adminPassword = adminPassword;
            return this;
        }

        public CompanyInfoBuilder withPublicDomain(String publicDomain)
        {
            companyInfo.publicDomain = publicDomain;
            return this;
        }

        public CompanyInfoBuilder withSysReport(String sysReport)
        {
            companyInfo.sysReport = sysReport;
            return this;
        }

        public static CompanyInfoBuilder companyInfo()
        {
            return new CompanyInfoBuilder();
        }

        public CompanyInfo build()
        {
            return companyInfo;
        }
    }
}
