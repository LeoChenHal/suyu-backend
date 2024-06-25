package com.lch.suyu.Result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {

    private Integer code;//返回编码，0代表成功，1代表失败

    private String msg;//返回信息

    private T data;//返回给前端接收的数据
//创建一个success泛型方法，简化后端代码返回的数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code=0;
        result.data=data;
        return result;
    }
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code=0;
        return result;
    }
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code=1;
        result.msg=msg;
        return result;
    }

}
