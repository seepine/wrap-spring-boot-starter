package com.seepine.wrap.entity;

/**
 * @author seepine
 * @since 0.2.0
 */
public class WrapProperties {
  String[] scanPackages;
  Integer status;
  Integer exceptionAdviceOrder;
  Integer responseAdviceOrder;
  // order设为-1000，比默认的0优先级高，又有一定的弹性空间方便用户创建比此优先级高的advice
  private static final Integer DEFAULT_ORDER = -1000;
  // 暂不支持自定义
  public static final String NOT_WRAP_HEADER = "not-wrap";
  public static final String NOT_WRAP_HEADER2 = "Not-Wrap";

  public WrapProperties(
      Integer status,
      String[] scanPackages,
      Integer exceptionAdviceOrder,
      Integer responseAdviceOrder) {
    this.status = status == null ? 200 : status;
    this.scanPackages = scanPackages == null ? new String[0] : scanPackages;
    this.exceptionAdviceOrder = exceptionAdviceOrder == null ? DEFAULT_ORDER : exceptionAdviceOrder;
    this.responseAdviceOrder = responseAdviceOrder == null ? DEFAULT_ORDER : responseAdviceOrder;
  }

  public String[] getScanPackages() {
    return scanPackages;
  }

  public Integer getStatus() {
    return status;
  }

  public Integer getExceptionAdviceOrder() {
    return exceptionAdviceOrder;
  }

  public Integer getResponseAdviceOrder() {
    return responseAdviceOrder;
  }
}
