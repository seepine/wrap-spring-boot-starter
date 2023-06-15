package com.seepine.wrap;

import com.seepine.tool.R;
import com.seepine.wrap.annotation.NotWrap;
import com.seepine.wrap.entity.WrapProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author seepine
 * @since 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class WrapResponseBodyAdvice implements ResponseBodyAdvice<Object>, Ordered {

  @Resource
  private WrapProperties wrapProperties;

  @Override
  public int getOrder() {
    return wrapProperties.getResponseAdviceOrder();
  }

  @Override
  // 返回 true 则下面 beforeBodyWrite方法被调用, 否则就不调用下述方法
  public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
    try {
      Method method = methodParameter.getMethod();
      if (method == null) {
        return false;
      }
      // 加了NotWrap注解的不封装返回值
      if (method.isAnnotationPresent(NotWrap.class)
          || method.getDeclaringClass().isAnnotationPresent(NotWrap.class)) {
        return false;
      }
      // 若有指定扫描包路径
      if (wrapProperties.getScanPackages() != null && wrapProperties.getScanPackages().length > 0) {
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
      @Nullable Object body,
      MethodParameter methodParameter,
      MediaType mediaType,
      Class<? extends HttpMessageConverter<?>> aClass,
      ServerHttpRequest serverHttpRequest,
      ServerHttpResponse serverHttpResponse) {
    // 请求头或相应头包含not-wrap则不包装
    if (judgeHeader(serverHttpRequest.getHeaders())
        || judgeHeader(serverHttpResponse.getHeaders())) {
      if (body instanceof R) {
        return ((R<?>) body).getData();
      }
      return body;
    }
    try {
      ServletServerHttpResponse res = (ServletServerHttpResponse) serverHttpResponse;
      res.getServletResponse().setContentType("application/json;charset=UTF-8");
    } catch (Exception ignored) {
    }
    if (body instanceof R) {
      return body;
    } else if (body instanceof String) {
      return "{\n"
          + "  \"code\": "
          + R.SUCCESS
          + ",\n"
          + "  \"data\": \""
          + body
          + "\",\n"
          + "  \"msg\": \"\"\n"
          + "}";
    }
    return R.ok(body);
  }

  private boolean judgeHeader(HttpHeaders headers) {
    return headers.containsKey(WrapProperties.NOT_WRAP_HEADER)
        || headers.containsKey(WrapProperties.NOT_WRAP_HEADER2);
  }
}
