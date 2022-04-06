package com.seepine.wrap;

import com.seepine.wrap.entity.R;
import com.seepine.wrap.entity.WrapProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author seepine
 * @since 0.3.0
 */
@Slf4j
@RestControllerAdvice
public class WrapExceptionHandler implements Ordered {

  @Resource private WrapProperties wrapProperties;

  @Override
  public int getOrder() {
    return wrapProperties.getExceptionAdviceOrder();
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
  public R<String> handleBindException(BindException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    return R.failed(e.getAllErrors().get(0).getDefaultMessage());
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public Object httpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletResponse response) {
    log.warn(e.getMessage());
    response.setStatus(wrapProperties.getStatus());
    if (e.getMessage() != null
        && e.getMessage().contains("Cannot deserialize value of type `long` from String")) {
      return R.failed("接口参数格式错误，请输入纯数字");
    }
    return R.failed("接口参数格式错误");
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public Object methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e, HttpServletResponse response) {
    log.warn(e.getMessage());
    response.setStatus(wrapProperties.getStatus());
    if (e.getMessage() != null
        && e.getMessage()
            .contains(
                "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")) {
      return R.failed("接口参数格式错误，请输入纯数字");
    }
    return R.failed("接口参数格式错误");
  }

  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public Object httpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e, HttpServletResponse response) {
    log.warn(e.getMessage());
    response.setStatus(wrapProperties.getStatus());
    return R.failed(e.getMessage());
  }
}
