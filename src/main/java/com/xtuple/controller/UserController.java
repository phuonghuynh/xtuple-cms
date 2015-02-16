package com.xtuple.controller;

import com.xtuple.dto.Result;
import com.xtuple.dto.UserInfo;
import com.xtuple.entity.User;
import org.jasypt.util.text.TextEncryptor;
import org.javalite.activejdbc.Base;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by phuonghqh on 2/15/15.
 */
@Controller
public class UserController {

  @Value("${db.host}")
  private String dbHost;

  @Value("${db.username}")
  private String dbUsername;

  @Value("${db.password}")
  private String dbPassword;

  @Resource
  private TextEncryptor textEncryptor;

  @ResponseBody
  @RequestMapping(value = "/signIn", method = RequestMethod.POST)
  public Result signIn(@RequestBody UserInfo userInfo, HttpServletResponse httpServletResponse) {
    Base.open("org.postgresql.Driver", dbHost, dbUsername, dbPassword);
    User user = User.findFirst("_username = ?", userInfo.getUsername());
    Result result = new Result(400, "Wrong user, " + userInfo.getUsername() + "!", null);
    if (user != null && textEncryptor.decrypt(user.get("_password").toString()).equals(userInfo.getPassword())) {
      httpServletResponse.addCookie(new Cookie("common", textEncryptor.encrypt(userInfo.getPassword())));
      userInfo.setPassword(null);
      result = new Result(200, "Welcome back, " + userInfo.getUsername() + "!", userInfo);
    }
    Base.close();
    return result;
  }


  @ResponseBody
  @RequestMapping(value = "/setting", method = RequestMethod.POST)
  public Result update(@RequestBody UserInfo userInfo) {
    Base.open("org.postgresql.Driver", dbHost, dbUsername, dbPassword);
    User user = User.findFirst("_username = ?", userInfo.getUsername());
    Result result = new Result(400, "Wrong user, " + userInfo.getUsername() + "!", null);
    if (user != null && textEncryptor.decrypt(user.get("_password").toString()).equals(userInfo.getOriginalPassword())) {
      user.set("_password", textEncryptor.encrypt(userInfo.getPassword())).saveIt();
      result = new Result(200, "Update successful, " + userInfo.getUsername() + "!", null);
    }
    Base.close();
    return result;
  }
}
