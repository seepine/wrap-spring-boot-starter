package com.seepine.wrap;

/**
 * @author wraptor
 * @since 0.0.1
 */
public class WrapException extends Exception {
    final Integer status;
    String message;
    Object data;

    public WrapException(String message) {
        this.status = Status.WORKFLOW_ERROR;
        this.message = message;
    }

    public WrapException(Object data, String message) {
        this.status = Status.WORKFLOW_ERROR;
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


    public interface Status {
        // 业务流程错误，可能参数错误，可能流程错误，可能逻辑错误
        int WORKFLOW_ERROR = 550;
    }

}
