package com.seepine.wrap;

/**
 * @author wraptor
 * @since 0.0.1
 */
public class WrapException extends RuntimeException {
  Integer status;
  String message;
  Object data;

  public WrapException(String message) {
    this.message = message;
  }

  public WrapException(Object data, String message) {
    this.data = data;
    this.message = message;
  }

  public WrapException(Integer status, Object data, String message) {
    this.status = status;
    this.message = message;
    this.data = data;
  }

  public WrapException(Integer status, String msg) {
    this.status = status;
    this.message = msg;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public Integer getStatus() {
    return status;
  }

  public Object getData() {
    return data;
  }
}
