package com.seepine.wrap;

import cn.hutool.json.JSONUtil;
import com.seepine.wrap.annotation.NotWrap;
import com.seepine.wrap.entity.R;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author wraptor
 * @since 0.0.1
 */
@Order(Integer.MIN_VALUE)
@RestControllerAdvice
public class WrapResponseBodyAdvice implements ResponseBodyAdvice {
    @Override
    public boolean supports(MethodParameter methodParameter, Class aClass) {
        try {
            Method method = methodParameter.getMethod();
            if (method == null) {
                return true;
            }
            // 加了NotWrap注解的才不封装返回值
            if (method.isAnnotationPresent(NotWrap.class) || method.getDeclaringClass().isAnnotationPresent(NotWrap.class)) {
                return false;
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof R) {
            return body;
        } else if (body instanceof String) {
            return JSONUtil.toJsonStr(R.ok(body));
        }
        return R.ok(body);
    }

    @ExceptionHandler(WrapException.class)
    public Object workflowException(final WrapException e, HttpServletResponse response) {
        response.setStatus(e.getStatus());
        return R.failed(e.getData(), e.getMessage());
    }
}
