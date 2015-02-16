package com.xtuple.dto;

/**
 * Created by phuonghqh on 2/16/15.
 */
public class Result {
  private Integer status;

  private String message;

  private Object hit;

  public Result(Integer status, String message, Object hit) {
    this.status = status;
    this.message = message;
    this.hit = hit;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public Object getHit() {
    return hit;
  }

  public void setHit(Object hit) {
    this.hit = hit;
  }
}
