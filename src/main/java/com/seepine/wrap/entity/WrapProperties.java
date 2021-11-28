package com.seepine.wrap.entity;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author seepine
 * @since 0.2.0
 */
@ConfigurationProperties(prefix = "wrap")
public class WrapProperties {
  String[] scanPackages;
  Integer status = 550;

  public String[] getScanPackages() {
    return scanPackages;
  }

  public void setScanPackages(String[] scanPackages) {
    this.scanPackages = scanPackages;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }
}
