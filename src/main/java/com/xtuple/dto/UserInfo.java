package com.xtuple.dto;

/**
 * Created by phuonghqh on 2/16/15.
 */
public class UserInfo {
  private String originalPassword;

  private String username;

  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getOriginalPassword() {
    return originalPassword;
  }

  public void setOriginalPassword(String originalPassword) {
    this.originalPassword = originalPassword;
  }
}
