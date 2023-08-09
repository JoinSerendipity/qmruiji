package com.qmkf.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * Author：qm
 *
 * @Description：
 */
@Data
public class Result<T> {
    private Integer code;
    private T data;
    private String msg;

    private Map map = new HashMap();

    public static <T> Result<T> success(T object) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        return r;
    }
    public static <T> Result<T> success(T object,String msg) {
        Result<T> r = new Result<T>();
        r.data = object;
        r.code = 1;
        r.msg = msg;
        return r;
    }

    public static <T> Result<T> error(String msg) {
        Result r = new Result();
        r.msg = msg;
        r.code = 0;
        return r;
    }

    public Result<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
