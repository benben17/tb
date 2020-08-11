package com.spacex.tb.common;

/**
 * 错误码
 *
 * @author Chunfu.Dong
 * @date 2019-08-03 11:07
 */
public enum ErrorCodeEnum {
    SYSTEM_ERROR(100000, "系统错误"),
    TOKEN_ERROR(100001, "Token错误"),
    LOGIN_ERROR(100002, "登陆用户错误"),
    PARAM_ERROR(100003, "参数错误"),
    DATA_ERROR(100004, "数据重复"),
    DATA_MISMATCHED(100100, "数据格式不匹配");



    private int code;

    private String msg;

    ErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}