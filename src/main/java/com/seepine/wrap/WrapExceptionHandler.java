package com.seepine.wrap;

import com.seepine.tool.R;
import com.seepine.wrap.entity.WrapProperties;
import org.springframework.core.Ordered;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * @author seepine
 * @since 0.3.0
 */
@RestControllerAdvice
public class WrapExceptionHandler implements Ordered {
  private final static String APPLICATION_JSON = "application/json;charset=UTF-8";

  private final WrapProperties wrapProperties;

  public WrapExceptionHandler(WrapProperties wrapProperties) {
    this.wrapProperties = wrapProperties;
  }

  @Override
  public int getOrder() {
    return wrapProperties.getExceptionAdviceOrder();
  }

  /**
   * 拦截流程异常 WrapException
   *
   * @param e        WrapException
   * @param response res
   * @return R
   */
  @ExceptionHandler(WrapException.class)
  public R<?> workflowException(final WrapException e, HttpServletResponse response) {
    response.setStatus(e.getStatus() == null ? wrapProperties.getStatus() : e.getStatus());
    response.setContentType(APPLICATION_JSON);
    return R.fail(e.getData(), e.getMessage());
  }

  /**
   * 拦截绑定参数校验提示语
   *
   * @param e        e
   * @param response res
   * @return R
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public R<?> methodArgumentNotValidException(
      MethodArgumentNotValidException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    response.setContentType(APPLICATION_JSON);
    return R.fail(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
  }

  /**
   * 拦截绑定参数校验提示语
   *
   * @param e        e
   * @param response res
   * @return R
   */
  @ExceptionHandler(BindException.class)
  public R<?> handleBindException(BindException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    response.setContentType(APPLICATION_JSON);
    return R.fail(e.getAllErrors().get(0).getDefaultMessage());
  }

  /**
   * 拦截接口请求参数格式错误友好提示
   *
   * @param e        e
   * @param response res
   * @return R
   */
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public R<?> httpMessageNotReadableException(
      HttpMessageNotReadableException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    response.setContentType(APPLICATION_JSON);
    if (e.getMessage() != null
        && e.getMessage().contains("Cannot deserialize value of type `long` from String")) {
      return R.fail("接口参数格式错误，请输入纯数字");
    }
    return R.fail("接口参数格式错误");
  }

  /**
   * 拦截接口请求参数格式错误友好提示
   *
   * @param e        e
   * @param response res
   * @return R
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public R<?> methodArgumentTypeMismatchException(
      MethodArgumentTypeMismatchException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    response.setContentType(APPLICATION_JSON);
    if (e.getMessage() != null
        && e.getMessage()
            .contains(
                "Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")) {
      return R.fail("接口参数格式错误，请输入纯数字");
    }
    return R.fail("接口参数格式错误");
  }

  /**
   * 拦截接口请求方法错误友好提示
   *
   * @param e        e
   * @param response res
   * @return R
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  public R<?> httpRequestMethodNotSupportedException(
      HttpRequestMethodNotSupportedException e, HttpServletResponse response) {
    response.setStatus(wrapProperties.getStatus());
    response.setContentType(APPLICATION_JSON);
    return R.fail(e.getMessage());
  }
}
