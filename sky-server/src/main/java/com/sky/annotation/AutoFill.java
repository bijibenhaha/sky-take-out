package com.sky.annotation;


import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD) // 规定此注解 定义在方法上
@Retention(RetentionPolicy.RUNTIME) // 固定写法
public @interface AutoFill {
    // 定义作用的 数据库操作类型
    OperationType value();
}
