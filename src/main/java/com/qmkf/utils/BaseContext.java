package com.qmkf.utils;

/**
 * Author：qm
 *
 * @Description：基于TheadLocal
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    public static Long getCurrentId(){
        Long aLong = threadLocal.get();
        return aLong;
    }
}
