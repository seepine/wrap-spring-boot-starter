package com.seepine.wrap;

import cn.hutool.json.JSONUtil;
import com.seepine.wrap.annotation.NotWrap;
import com.seepine.wrap.entity.R;
import com.seepine.wrap.entity.WrapProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author seepine
 * @since 0.0.1
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
@EnableConfigurationProperties({WrapProperties.class})
public class WrapResponseBodyAdvice implements ResponseBodyAdvice {

  private final WrapProperties wrapProperties;

  public WrapResponseBodyAdvice(WrapProperties wrapProperties) {
    this.wrapProperties = wrapProperties;
  }

  @Override
  public boolean supports(MethodParameter methodParameter, Class aClass) {
    try {
      Method method = methodParameter.getMethod();
      if (method == null) {
        return true;
      }
      // 加了NotWrap注解的才不封装返回值
      if (method.isAnnotationPresent(NotWrap.class)
          || method.getDeclaringClass().isAnnotationPresent(NotWrap.class)) {
        return false;
      }
      // 若有指定扫描包路径
      if (wrapProperties.getScanPackages() != null) {
        String packagePath = method.getDeclaringClass().getPackage().getName();
        for (String scanPackage : wrapProperties.getScanPackages()) {
          if (packagePath.startsWith(scanPackage)) {
            return true;
          }
        }
        return false;
      }
    } catch (Exception ignored) {
    }
    return true;
  }

  @Override
  public Object beforeBodyWrite(
      Object body,
      MethodParameter methodParameter,
      MediaType mediaType,
      Class aClass,
      ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    if (body instanceof R) {
      return body;
    } else if (body instanceof String) {
      return JSONUtil.toJsonStr(R.ok(body));
    }
    return R.ok(body);
  }

  @ExceptionHandler(WrapException.class)
  public Object workflowException(final WrapException e, HttpServletResponse response) {
    response.setStatus(e.getStatus() == null ? wrapProperties.getStatus() : e.getStatus());
    return R.failed(e.getData(), e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public R<String> illegalArgumentException(
      IllegalArgumentException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    return R.failed(e.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public R<String> methodArgumentNotValidException(
      MethodArgumentNotValidException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    return R.failed(
        Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  @ExceptionHandler(BindException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public R<String> handleBindException(BindException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    return R.failed(e.getAllErrors().get(0).getDefaultMessage());
  }
}
