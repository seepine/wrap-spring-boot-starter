package com.seepine.wrap.config;

import com.seepine.wrap.entity.WrapProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * @author seepine
 * @since 0.3.0
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class WrapConfigurer {
  @Value("${wrap.status:}")
  private Integer status;

  @Value("${wrap.scan-packages:}")
  private String[] scanPackages;

  @Value("${wrap.exception-advice-order:}")
  private Integer exceptionAdviceOrder;

  @Value("${wrap.response-advice-order:}")
  private Integer responseAdviceOrder;

  @Bean
  @ConditionalOnMissingBean(WrapProperties.class)
  public WrapProperties wrapProperties() {
    return new WrapProperties(status, scanPackages, exceptionAdviceOrder, responseAdviceOrder);
  }
}
