package com.seepine.wrap.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.io.Serializable;

/**
 * 相应主体
 *
 * @author wraptor
 * @since 0.0.1
 */
//@JsonInclude(JsonInclude.Include.NON_NULL)
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final int SUCCESS = 0;
    private static final int FAIL = -1;

    private int code;

    private String msg;

    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> R<T> ok() {
        return build(null, SUCCESS, null);
    }

    public static <T> R<T> ok(T data) {
        return build(data, SUCCESS, null);
    }

    public static <T> R<T> ok(T data, String msg) {
        return build(data, SUCCESS, msg);
    }

    public static <T> R<T> failed() {
        return build(null, FAIL, null);
    }

    public static <T> R<T> failed(String msg) {
        return build(null, FAIL, msg);
    }

    public static <T> R<T> failed(T data) {
        return build(data, FAIL, null);
    }

    public static <T> R<T> failed(T data, String msg) {
        return build(data, FAIL, msg);
    }

    private static <T> R<T> build(T data, int code, String msg) {
        R<T> apiResult = new R<>();
        apiResult.setCode(code);
        apiResult.setData(data);
        apiResult.setMsg(msg);
        return apiResult;
    }
}
